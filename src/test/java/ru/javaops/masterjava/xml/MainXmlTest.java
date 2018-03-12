package ru.javaops.masterjava.xml;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import ru.javaops.masterjava.xml.schema.User;

public class MainXmlTest {

  private static final MainXml MAIN_XML = new MainXml();
  private static final String MASTER_JAVA = "masterjava";
  private static final String TOP_JAVA = "topjava";
  private static final String ADMIN = "Admin";
  private static final String USER = "Full Name";

  @Test
  public void testGetUserByProjectWithJaxb() throws Exception {
    List<User> users = MAIN_XML.getUserByProjectWithJaxb(TOP_JAVA);
    assertEquals(users.size(), 2);
    assertEquals(users.get(0).getFullName(), ADMIN);
    assertEquals(users.get(1).getFullName(), USER);
    users = MAIN_XML.getUserByProjectWithJaxb(MASTER_JAVA);
    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getFullName(), ADMIN);
  }

  @Test
  public void getUserByProjectWithStax() throws Exception{
    MAIN_XML.getUserByProjectWithStax(TOP_JAVA);
  }
}