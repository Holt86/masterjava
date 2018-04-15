package ru.javaops.masterjava.persist.dao;


import static ru.javaops.masterjava.persist.data.UserGroupTestData.ADMIN_GROUPS;
import static ru.javaops.masterjava.persist.data.UserGroupTestData.ADMIN_MASTER_1;
import static ru.javaops.masterjava.persist.data.UserGroupTestData.USER_1_MASTER_1;
import static ru.javaops.masterjava.persist.data.UserGroupTestData.USER_1_TOP_6;
import static ru.javaops.masterjava.persist.data.UserTestData.ADMIN;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.UserGroupTestData;


public class UserGroupDaoTest extends AbstractDaoTest<UserGroupDao> {

  public UserGroupDaoTest() {
    super(UserGroupDao.class);
  }

  @BeforeClass
  public static void init() throws Exception {
    UserGroupTestData.init();
  }

  @Before
  public void setUp() throws Exception {
    UserGroupTestData.setUp();
  }

  @Test
  public void testClean() throws Exception {
    dao.clean();
    Assert.assertTrue(dao.getWithLimit(5).isEmpty());
  }

  @Test
  public void testInsert() throws Exception {
    dao.clean();
    dao.insert(USER_1_MASTER_1);
    Assert.assertEquals(USER_1_MASTER_1, dao.getWithLimit(1).get(0));
  }

  @Test
  public void testInsertButch() throws Exception {
    dao.clean();
    dao.insertButch(ADMIN_GROUPS, 3);
    Assert.assertEquals(ADMIN_GROUPS, dao.getGroupForUser(ADMIN.getId()));
  }

  @Test
  public void testGetGroupForUser() throws Exception {
    Assert.assertEquals(ADMIN_GROUPS, dao.getGroupForUser(ADMIN.getId()));
  }

  @Test
  public void testGetWithLimit() throws Exception {
    Assert.assertEquals(Arrays.asList(ADMIN_MASTER_1, USER_1_MASTER_1, USER_1_TOP_6), dao.getWithLimit(3));
  }
}