package ru.javaops.masterjava.persist.data;

import static ru.javaops.masterjava.persist.data.UserTestData.*;

import com.google.common.collect.ImmutableList;
import java.util.List;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserGroupDao;
import ru.javaops.masterjava.persist.model.UserGroup;

public class UserGroupTestData {

  public static UserGroup ADMIN_TOP_7;
  public static UserGroup ADMIN_TOP_8;
  public static UserGroup ADMIN_MASTER_1;
  public static UserGroup USER_1_TOP_6;
  public static UserGroup USER_1_MASTER_1;
  public static List<UserGroup> ADMIN_GROUPS;
  public static List<UserGroup> USER_1_GROUPS;

  public static void init() {
    UserTestData.init();
    UserTestData.setUp();
    ProjectTestData.init();
    ProjectTestData.setUp();
    GroupTestData.init();
    GroupTestData.setUp();

    ADMIN_TOP_7 = new UserGroup(ADMIN.getId(), "topjava07");
    ADMIN_TOP_8 = new UserGroup(ADMIN.getId(), "topjava08");
    ADMIN_MASTER_1 = new UserGroup(ADMIN.getId(), "masterjava01");
    USER_1_TOP_6 = new UserGroup(USER1.getId(), "topjava06");
    USER_1_MASTER_1 = new UserGroup(USER1.getId(), "masterjava01");
    ADMIN_GROUPS = ImmutableList.of(ADMIN_MASTER_1, ADMIN_TOP_7, ADMIN_TOP_8);
    USER_1_GROUPS = ImmutableList.of(USER_1_TOP_6, USER_1_MASTER_1);
  }

  public static void setUp() {
    UserGroupDao dao = DBIProvider.getDao(UserGroupDao.class);
    dao.clean();
    dao.insertButch(ADMIN_GROUPS, 3);
    USER_1_GROUPS.forEach(dao::insert);
  }
}
