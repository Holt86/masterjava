package ru.javaops.masterjava.persist.dao;


import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.model.City;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class CityDao implements AbstractDao{

  public City insert(City city){
    if (city.isNew()){
      int id = insertGeneratedId(city);
      city.setId(id);
    }else {
      insertWithId(city);
    }
    return city;
  }

  @SqlQuery("SELECT nextval('general_seq')")
  abstract int getNextVal();

  @Transaction
  public int getSeqAndSkip(int step) {
    int id = getNextVal();
    DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE general_seq RESTART WITH " + (id + step)));
    return id;
  }

  @SqlUpdate("TRUNCATE cities CASCADE")
  @Override
  public abstract void clean();

  @SqlQuery("SELECT * FROM cities ORDER BY full_name, name LIMIT :it")
  public abstract List<City> getWithLimit(@Bind int limit);

  @SqlUpdate("INSERT INTO cities (name, full_name) VALUES (:name, :fullName) ")
  @GetGeneratedKeys
  abstract int insertGeneratedId(@BindBean City city);

  @SqlUpdate("INSERT INTO cities (id, name, full_name) VALUES (:id, :name, :fullName) ")
  abstract void insertWithId(@BindBean City city);

  //    https://habrahabr.ru/post/264281/
  @SqlBatch("INSERT INTO cities (id, name, full_name) VALUES (:id, :name, :fullName) " +
      "ON CONFLICT DO NOTHING")
  public abstract int[] insertBatch(@BindBean List<City> cities, @BatchChunkSize int chunkSize);
}
