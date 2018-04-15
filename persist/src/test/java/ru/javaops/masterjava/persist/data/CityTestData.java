package ru.javaops.masterjava.persist.data;

import com.google.common.collect.ImmutableList;
import java.util.List;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;

public class CityTestData {

  public static City SPB;
  public static City MSK;
  public static City KIV;
  public static City MNSK;
  public static List<City> CITIES;

  public static void init(){
    SPB = new City("spb", "Санкт-Петербург");
    MSK = new City("mow", "Москва");
    KIV = new City("kiv", "Киев");
    MNSK = new City("mnsk", "Минск");
    CITIES = ImmutableList.of(KIV, MNSK, MSK, SPB);
  }

  public static void setUp(){
    CityDao dao = DBIProvider.getDao(CityDao.class);
    dao.clean();
    DBIProvider.getDBI().useTransaction(((conn, status) -> {
      CITIES.forEach(dao::insert);
    }));
  }
}
