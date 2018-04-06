package ru.javaops.masterjava.persist.dao;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.UserTestData;
import ru.javaops.masterjava.persist.model.User;

import java.util.List;

import static ru.javaops.masterjava.persist.UserTestData.FIST5_USERS;
import static ru.javaops.masterjava.persist.UserTestData.USER3;

public class UserDaoTest extends AbstractDaoTest<UserDao> {

    public UserDaoTest() {
        super(UserDao.class);
    }

    @BeforeClass
    public static void init() throws Exception {
        UserTestData.init();
    }

    @Before
    public void setUp() throws Exception {
        UserTestData.setUp();
    }

    @Test
    public void getWithLimit() {
        List<User> users = dao.getWithLimit(5);
        Assert.assertEquals(FIST5_USERS, users);
    }

    @Test
    public void testInsertBatch() throws Exception {
        int[] actual1 = {1, 1, 1, 1, 1,};
        int[] actual2 = {0, 0, 0, 0, 0, 1};
        dao.clean();
        List<User> users = new ArrayList<>(UserTestData.FIST5_USERS);
        int[] expected =  dao.insertBatch(users, 5);
        Assert.assertArrayEquals(expected, actual1);
        users.add(USER3);
        expected =  dao.insertBatch(users, 6);
        Assert.assertArrayEquals(expected, actual2);
    }
}