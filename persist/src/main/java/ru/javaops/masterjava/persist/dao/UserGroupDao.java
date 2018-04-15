package ru.javaops.masterjava.persist.dao;


import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.UserGroup;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class UserGroupDao implements AbstractDao{

  @SqlUpdate("TRUNCATE user_group CASCADE")
  @Override
  public abstract void clean();

  @SqlUpdate("INSERT INTO user_group (user_id, group_name) VALUES (:userId, :groupName) ")
  public abstract void insert(@BindBean UserGroup userGroup);

  @SqlBatch("INSERT INTO user_group (user_id, group_name) VALUES (:userId, :groupName) " +
      "ON CONFLICT DO NOTHING")
  public abstract int[] insertButch(@BindBean List<UserGroup> groupsName, @BatchChunkSize int chunkSize);

  @SqlQuery("SELECT * FROM user_group WHERE user_id IN (:it)")
  public abstract List<UserGroup> getGroupForUser(@Bind int userId);

  @SqlQuery("SELECT * FROM user_group ORDER BY group_name LIMIT :it")
  public abstract List<UserGroup> getWithLimit(@Bind int limit);
}
