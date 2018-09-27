package ru.javaops.masterjava.upload;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.util.Pair;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
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

  /*
   * return failed users chunks
   */
  public List<FailedEmails> process(final StaxStreamProcessor processor, Map<String, City> cities,
      Map<String, Group> groups, int chunkSize) throws XMLStreamException, JAXBException {
    log.info("Start processing with chunkSize=" + chunkSize);

//    Map<String, List<String>> chunkFutures = new LinkedHashMap<>();  // ordered map (emailRange -> chunk future)
    Map<Pair<String, Future<List<String>>>, Map<String, List<UserGroup>>> chunkFutures = new LinkedHashMap<>();

    int id = userDao.getSeqAndSkip(chunkSize);
    List<User> chunkUsers = new ArrayList<>(chunkSize);
    Map<String, List<UserGroup>> chunkUserGroups = new HashMap<>();

    val unmarshaller = jaxbParser.createUnmarshaller();
    List<FailedEmails> failed = new ArrayList<>();

    label:
    while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
      String cityRef = processor.getAttribute("city");  // unmarshal doesn't get city ref
      String groupsRef = processor.getAttribute("groupRefs");
      List<Group> groupsList = new ArrayList<>();
      ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller
          .unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);

      if (groupsRef != null) {
        for (String groupRef : groupsRef.split(" ")) {
          Group group = groups.get(groupRef);
          if (group == null) {
            failed.add(new FailedEmails(xmlUser.getEmail(),
                "Group '" + groupRef + "' is not present in DB"));
            continue label;
          }
          groupsList.add(group);
        }
      }
      if (cities.get(cityRef) == null) {
        failed.add(
            new FailedEmails(xmlUser.getEmail(), "City '" + cityRef + "' is not present in DB"));
      } else {
        final User user = new User(id++, xmlUser.getValue(), xmlUser.getEmail(),
            UserFlag.valueOf(xmlUser.getFlag().value()), cityRef);
        chunkUserGroups.put(user.getEmail(),
            groupsList.stream().map(gr -> new UserGroup(user.getId(), gr.getId())).collect(
                toList()));
        chunkUsers.add(user);
        if (chunkUsers.size() == chunkSize) {
          addChunkFutures(chunkFutures, chunkUsers, chunkUserGroups);
          chunkUsers = new ArrayList<>(chunkSize);
          chunkUserGroups = new HashMap<>();
          id = userDao.getSeqAndSkip(chunkSize);
        }
      }
    }

    if (!chunkUsers.isEmpty()) {
      addChunkFutures(chunkFutures, chunkUsers, chunkUserGroups);
    }

    List<String> allAlreadyPresents = new ArrayList<>();
    chunkFutures.forEach((pairFuture, userGroups) -> {
      try {
        List<String> alreadyPresentsInChunk = pairFuture.getValue().get();
        log.info("{} successfully executed with already presents: {}", pairFuture.getKey(),
            alreadyPresentsInChunk);
        allAlreadyPresents.addAll(alreadyPresentsInChunk);
        alreadyPresentsInChunk.forEach(userGroups::remove);
      } catch (InterruptedException | ExecutionException e) {
        log.error(pairFuture.getKey() + " failed", e);
        failed.add(new FailedEmails(pairFuture.getKey(), e.toString()));
        chunkFutures.remove(pairFuture);
      }
    });
    if (!allAlreadyPresents.isEmpty()) {
      failed.add(new FailedEmails(allAlreadyPresents.toString(), "already presents"));
    }
    List<UserGroup> userGroups = new ArrayList<>();

    chunkFutures.values().forEach(e -> e.values().forEach(userGroups::addAll));
    executorService.submit(() -> userGroupDao.insertBatch(userGroups));
    return failed;
  }

  private void addChunkFutures(
      Map<Pair<String, Future<List<String>>>, Map<String, List<UserGroup>>> chunkFutures,
      List<User> chunkUsers, Map<String, List<UserGroup>> chunkUsersGroups) {
    String emailRange = String.format("[%s-%s]", chunkUsers.get(0).getEmail(),
        chunkUsers.get(chunkUsers.size() - 1).getEmail());
    Future<List<String>> future = executorService
        .submit(() -> userDao.insertAndGetConflictEmails(chunkUsers));

    chunkFutures.put(new Pair<>(emailRange, future), chunkUsersGroups);
    log.info("Submit chunk: " + emailRange);
  }
}
