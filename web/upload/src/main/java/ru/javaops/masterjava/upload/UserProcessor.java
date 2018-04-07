package ru.javaops.masterjava.upload;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

public class UserProcessor {

  private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
  private static final ExecutorService service = Executors.newFixedThreadPool(4);
  //  private static final CompletionService<Result> completionService = new ExecutorCompletionService<>(service);
  private static final UserDao userDao = DBIProvider.getDao(UserDao.class);

  public List<User> process(final InputStream is, final int chunkSize)
      throws XMLStreamException, JAXBException {
    final CompletionService<Result> completionService = new ExecutorCompletionService<>(service);
    final StaxStreamProcessor processor = new StaxStreamProcessor(is);
    List<User> users = new ArrayList<>();
    List<User> chunkUsers = new ArrayList<>();
    List<Future<Result>> futures = new ArrayList<>();
    List<User> returnedUsers = new ArrayList<>();
    int index = 0;

    JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
    while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
      ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller
          .unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
      final User user = new User(xmlUser.getValue(), xmlUser.getEmail(),
          UserFlag.valueOf(xmlUser.getFlag().value()));
      users.add(user);
      chunkUsers.add(user);
      index++;

      if (chunkUsers.size() == chunkSize) {
        futures.add(completionService
            .submit(new CallableUser(chunkSize, index - chunkUsers.size(), chunkUsers)));
        chunkUsers = new ArrayList<>();
      }
    }
    futures.add(
        completionService
            .submit(new CallableUser(chunkUsers.size(), index - index % chunkSize, chunkUsers)));

    while (!futures.isEmpty()) {
      try {
        Future<Result> future = completionService.poll(10, TimeUnit.SECONDS);
        if (future == null) {
          throw new IllegalArgumentException("Timeout");
        }
        futures.remove(future);
        Result result = future.get();
        int start = result.startChunk;
        int end = result.startChunk + result.arr.length;
        for (int i = start; i < end; i++) {
          if (i == start || i == end - 1 || result.arr[i - start] == 0) {
            returnedUsers.add(users.get(i));
          }
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    return returnedUsers;
  }

  private static class Result {

    private int startChunk;
    int[] arr;

    public Result(int startChunk, int[] arr) {
      this.startChunk = startChunk;
      this.arr = arr;
    }
  }

  private static class CallableUser implements Callable<Result> {

    private int sizeChunk;
    private int startChunk;
    private List<User> users;

    public CallableUser(int sizeChunk, int start, List<User> users) {
      this.sizeChunk = sizeChunk;
      this.startChunk = start;
      this.users = users;
    }

    @Override
    public Result call() throws Exception {
      return new Result(startChunk, userDao.insertBatch(users, sizeChunk));
    }
  }

  public ExecutorService getService() {
    return service;
  }
}
