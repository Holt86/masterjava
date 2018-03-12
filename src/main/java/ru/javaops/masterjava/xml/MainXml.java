package ru.javaops.masterjava.xml;


import static java.util.Arrays.asList;
import static java.util.Arrays.sort;

import com.google.common.io.Resources;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import ru.javaops.masterjava.xml.schema.Group;
import ru.javaops.masterjava.xml.schema.ObjectFactory;
import ru.javaops.masterjava.xml.schema.Payload;
import ru.javaops.masterjava.xml.schema.Project;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

public class MainXml {

  private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

  public List<User> getUserByProjectWithJaxb(String projectName){
    Payload payload = null;
    try {
      payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
    } catch (JAXBException e) {
      e.printStackTrace();
      System.out.println("Unmarshal exception");
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("IOexception");
    }
    List<User> users = payload.getUsers().getUser();
    return users.stream().filter(
        user -> user.getGroups().stream().anyMatch(
            obj -> ((Project)((Group) obj).getProject()).getName().equals(projectName))
    ).sorted((u1, u2) -> u1.getFullName().compareTo(u2.getFullName())).collect(Collectors.toList());

  }

  public void getUserByProjectWithStax(String projectName){
    try(StaxStreamProcessor processor = new StaxStreamProcessor(Resources.getResource("payload.xml").openStream())){
      XMLStreamReader reader = processor.getReader();
      while (reader.hasNext()){
        int event = reader.next();
        if (event == XMLEvent.START_ELEMENT){
          if ("User".equals(reader.getLocalName())){
            String attributes = reader.getAttributeValue(null, "groups");
            if (attributes == null)
              continue;
            String[] groups = attributes.split(" ");

            if (asList(groups).stream().anyMatch(group -> group.startsWith(projectName))){

              System.out.println("User - " + reader.getAttributeValue(null, "email") + " - " + processor.getElementValue("fullName"));;
            }
          }
        }
      }

    }catch (XMLStreamException e){
      e.printStackTrace();
      System.out.println("XMLStreamException");
    }catch (IOException e) {
      e.printStackTrace();
      System.out.println("IOexception");
    }
  }
}
