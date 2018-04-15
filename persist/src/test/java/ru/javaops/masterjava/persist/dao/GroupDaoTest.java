package ru.javaops.masterjava.persist.dao;

import static ru.javaops.masterjava.persist.data.GroupTestData.MASTER_01;
import static ru.javaops.masterjava.persist.data.GroupTestData.TOP_06;
import static ru.javaops.masterjava.persist.data.GroupTestData.TOP_07;
import static ru.javaops.masterjava.persist.data.GroupTestData.TOP_08;
import static ru.javaops.masterjava.persist.data.GroupTestData.TOP_GROUPS;
import static ru.javaops.masterjava.persist.data.ProjectTestData.TOPJAVA;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.GroupTestData;
import ru.javaops.masterjava.persist.model.Group;


public class GroupDaoTest extends AbstractDaoTest<GroupDao> {

  public GroupDaoTest() {
    super(GroupDao.class);
  }

  @BeforeClass
  public static void init() throws Exception {
    GroupTestData.init();
  }

  @Before
  public void setUp() throws Exception {
    GroupTestData.setUp();
  }

  @Test
  public void testInsert() throws Exception {
    dao.clean();
    TOP_06.setProjectId(TOPJAVA.getId());
    dao.insert(TOP_06);
    List<Group> groups = dao.getByProjectId(TOPJAVA.getId());
    Assert.assertEquals(1, groups.size());
    Assert.assertEquals(TOP_06, groups.get(0));
  }

  @Test
  public void testGetSeqAndSkip() throws Exception {
    int seq1 = dao.getSeqAndSkip(5);
    int seq2 = dao.getSeqAndSkip(1);
    Assert.assertEquals(5, seq2 - seq1);
  }

  @Test
  public void testClean() throws Exception {
    dao.clean();
    Assert.assertTrue(dao.getWithLimit(5).isEmpty());
  }

  @Test
  public void testGetWithLimit() throws Exception {
    List<Group> groups = dao.getWithLimit(4);
    Assert.assertEquals(Arrays.asList(MASTER_01, TOP_06, TOP_07, TOP_08), groups);
  }

  @Test
  public void testGetByProjectId() throws Exception {
    Assert.assertEquals(TOP_GROUPS, dao.getByProjectId(TOPJAVA.getId()));
  }

  @Test
  public void testInsertBatchForProject() throws Exception {
    dao.clean();
    dao.insertBatchForProject(TOP_GROUPS, TOPJAVA.getId(), 2);
    Assert.assertEquals(TOP_GROUPS, dao.getByProjectId(TOPJAVA.getId()));
  }
}