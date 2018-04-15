package ru.javaops.masterjava.persist.dao;

import static java.util.Arrays.asList;
import static ru.javaops.masterjava.persist.data.ProjectTestData.PROJECTS;
import static ru.javaops.masterjava.persist.data.ProjectTestData.TOPJAVA;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.ProjectTestData;


public class ProjectDaoTest extends AbstractDaoTest<ProjectDao> {

  public ProjectDaoTest() {
    super(ProjectDao.class);
  }

  @BeforeClass
  public static void init() throws Exception {
    ProjectTestData.init();
  }

  @Before
  public void setUp() throws Exception {
    ProjectTestData.setUp();
  }

  @Test
  public void testClean() throws Exception {
    dao.clean();
    Assert.assertEquals(0, dao.getWithLimit(5).size());

  }

  @Test
  public void testGetWithLimit() throws Exception {
   Assert.assertEquals(PROJECTS, dao.getWithLimit(2));
  }

  @Test
  public void testInsertGeneratedId() throws Exception {
    dao.clean();
    dao.insert(TOPJAVA);
    Assert.assertEquals(asList(TOPJAVA), dao.getWithLimit(1));
  }

  @Test
  public void testInsertWithId() throws Exception {
    dao.clean();
    TOPJAVA.setId(1);
    dao.insert(TOPJAVA);
    Assert.assertEquals(asList(TOPJAVA), dao.getWithLimit(1));
  }
}