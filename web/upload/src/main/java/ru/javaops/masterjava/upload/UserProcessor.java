package ru.javaops.masterjava.upload;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.JaxbUnmarshaller;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProcessor {

  private static final JaxbParser jaxbParser = new JaxbParser(ObjectFactory.class);
  private UserDao userDao = DBIProvider.getDao(UserDao.class);

  public List<User> process(final InputStream is, final int chunkSize)
      throws XMLStreamException, JAXBException {
    final StaxStreamProcessor processor = new StaxStreamProcessor(is);
    List<User> users = new ArrayList<>();

    JaxbUnmarshaller unmarshaller = jaxbParser.createUnmarshaller();
    while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
      ru.javaops.masterjava.xml.schema.User xmlUser = unmarshaller
          .unmarshal(processor.getReader(), ru.javaops.masterjava.xml.schema.User.class);
      final User user = new User(xmlUser.getValue(), xmlUser.getEmail(),
          UserFlag.valueOf(xmlUser.getFlag().value()));
      users.add(user);
    }
    int[] result = userDao.insertBatch(users, chunkSize);

    return IntStream.range(0, result.length)
        .filter(i -> result[i] == 0)
        .mapToObj(i -> users.get(i))
        .collect(Collectors.toList());
  }
}
