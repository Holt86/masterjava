package ru.javaops.masterjava.persist.dao;

import static ru.javaops.masterjava.persist.data.CityTestData.CITIES;
import static ru.javaops.masterjava.persist.data.CityTestData.SPB;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.javaops.masterjava.persist.data.CityTestData;
import ru.javaops.masterjava.persist.model.City;

public class CityDaoTest extends AbstractDaoTest<CityDao> {

  public CityDaoTest() {
    super(CityDao.class);
  }

  @BeforeClass
  public static void init() throws Exception {
    CityTestData.init();
  }

  @Before
  public void setUp() throws Exception {
    CityTestData.setUp();
  }

  @Test
  public void testClean() throws Exception {
    dao.clean();
    Assert.assertEquals(0, dao.getWithLimit(5).size());
  }

  @Test
  public void testGetWithLimit() throws Exception{
    List<City> cities = dao.getWithLimit(4);
    Assert.assertEquals(CITIES, cities);
  }

  @Test
  public void testInsertGeneratedId() throws Exception {
    dao.clean();
    CITIES.forEach(dao::insert);
    List<City> cities = dao.getWithLimit(4);
    Assert.assertEquals(CITIES, cities);
  }

  @Test
  public void testInsertWithId() throws Exception {
    dao.clean();
    SPB.setId(1);
    dao.insertWithId(SPB);
    List<City> cities = dao.getWithLimit(4);
    Assert.assertEquals(Arrays.asList(SPB), cities);
  }

  @Test
  public void testInsertButch() throws Exception{
    dao.clean();
    dao.insertBatch(CITIES, 5);
    List<City> cities = dao.getWithLimit(4);
    Assert.assertEquals(CITIES, cities);
  }

  @Test
  public void getSeqAndSkip() throws Exception {
    int seq1 = dao.getSeqAndSkip(5);
    int seq2 = dao.getSeqAndSkip(1);
    Assert.assertEquals(5, seq2 - seq1);
  }
}