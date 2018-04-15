package ru.javaops.masterjava.persist.data;


import static ru.javaops.masterjava.persist.data.ProjectTestData.MASTERJAVA;
import static ru.javaops.masterjava.persist.data.ProjectTestData.TOPJAVA;

import com.google.common.collect.ImmutableList;
import java.util.List;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.GroupDao;
import ru.javaops.masterjava.persist.model.Group;
import ru.javaops.masterjava.persist.model.GroupType;

public class GroupTestData {

  public static Group TOP_06;
  public static Group TOP_07;
  public static Group TOP_08;
  public static List<Group> TOP_GROUPS;
  public static Group MASTER_01;
  public static List<Group> MASTER_GROUPS;

  public static void init() {
    ProjectTestData.init();
    ProjectTestData.setUp();
    TOP_06 = new Group("topjava06", GroupType.FINISHED, TOPJAVA.getId());
    TOP_07 = new Group("topjava07", GroupType.FINISHED, TOPJAVA.getId());
    TOP_08 = new Group("topjava08", GroupType.CURRENT, TOPJAVA.getId());
    TOP_GROUPS = ImmutableList.of(TOP_06, TOP_07, TOP_08);
    MASTER_01 = new Group("masterjava01", GroupType.CURRENT, MASTERJAVA.getId());
    MASTER_GROUPS = ImmutableList.of(MASTER_01);
  }

  public static void setUp(){
    GroupDao dao = DBIProvider.getDao(GroupDao.class);
    dao.clean();
    TOP_GROUPS.forEach(dao::insert);
    dao.insert(MASTER_01);
  }
}
