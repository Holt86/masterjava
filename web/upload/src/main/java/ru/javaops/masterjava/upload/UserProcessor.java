package ru.javaops.masterjava.upload;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.BaseEntity;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserGroup;
import ru.javaops.masterjava.persist.model.type.UserFlag;
import ru.javaops.masterjava.upload.PayloadProcessor.FailedEmails;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

@Slf4j
public class UserProcessor {

  private static final int NUMBER_THREADS = 4;

  private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
  private static UserDao userDao = DBIProvider.getDao(UserDao.class);
  private static UserGroupDao userGroupDao = DBIProvider.getDao(UserGroupDao.class);

  private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREADS);

  @Value
  private static class ItemChunk {

    User user;
    List<UserGroup> userGroups;
  }

  /*
   * return failed users chunks
   */
  public List<FailedEmails> process(final StaxStreamProcessor processor, Map<String, City> cities,
      Map<String, Group> groups, int chunkSize) throws XMLStreamException, JAXBException {
    log.info("Start processing with chunkSize=" + chunkSize);

    Map<String, Future<List<String>>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)

    int id = userDao.getSeqAndSkip(chunkSize);
    List<ItemChunk> chunk = new ArrayList<>(chunkSize);
    val unmarshaller = jaxbParser.createUnmarshaller();
    List<FailedEmails> failed = new ArrayList<>();

    label:
    while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
      String cityRef = processor.getAttribute("city");  // unmarshal doesn't get city ref
      String groupRef = processor.getAttribute("groupRefs");
      String[] groupsRef = (groupRef == null) ?
          new String[0] :
          groupRef.split(" ");
      List<Group> groupList = new ArrayList<>();

      ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller
          .unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);

      for (String key : groupsRef){
        Group group = groups.get(key);
        if (group == null){
          failed.add(
              new FailedEmails(xmlUser.getEmail(), "Group '" + key + "' is not present in DB"));
          continue label;
        }
        groupList.add(group);
      }
        if (cities.get(cityRef) == null) {
          failed.add(
              new FailedEmails(xmlUser.getEmail(), "City '" + cityRef + "' is not present in DB"));
        } else {
          final User user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(),
              UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);

          chunk.add(new ItemChunk(user, groupList.stream()
              .map(gr -> new UserGroup(user.getId(), gr.getId()))
              .collect(toList())));

          if (chunk.size() == chunkSize) {
            addChunkFutures(chunkFutures, chunk);
            chunk = new ArrayList<>(chunkSize);
            id = userDao.getSeqAndSkip(chunkSize);
          }
        }
      }


    if (!chunk.isEmpty()) {
      addChunkFutures(chunkFutures, chunk);
    }

    List<String> allAlreadyPresents = new ArrayList<>();
    chunkFutures.forEach((emailRange, future) -> {
      try {
        List<String> alreadyPresentsInChunk = future.get();
        log.info("{} successfully executed with already presents: {}", emailRange,
            alreadyPresentsInChunk);
        allAlreadyPresents.addAll(alreadyPresentsInChunk);
      } catch (InterruptedException | ExecutionException e) {
        log.error(emailRange + " failed", e);
        failed.add(new FailedEmails(emailRange, e.toString()));
      }
    });
    if (!allAlreadyPresents.isEmpty()) {
      failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
    }
    return failed;
  }

  private void addChunkFutures(Map<String, Future<List<String>>> chunkFutures, List<ItemChunk> chunk) {
    String emailRange = String
        .format("[%s-%s]", chunk.get(0).getUser().getEmail(), chunk.get(chunk.size() - 1).getUser().getEmail());
    Future<List<String>> future = executorService
        .submit(() -> {
          List<User> presentUsers = userDao
              .insertAndGetConflictEmails(chunk.stream().map(ItemChunk::getUser).collect(toList()));
          Set<Integer> presentUsersId = presentUsers.stream().map(BaseEntity::getId).collect(toSet());
          userGroupDao.insertBatch(chunk.stream()
              .flatMap(ch -> ch.getUserGroups().stream())
              .filter(ch -> !presentUsersId.contains(ch.getUserId()))
              .collect(toList()));

          return presentUsers.stream().map(User::getEmail).collect(toList());
        });
    chunkFutures.put(emailRange, future);
    log.info("Submit chunk: " + emailRange);
  }
}
