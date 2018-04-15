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
import ru.javaops.masterjava.persist.model.Group;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class GroupDao implements AbstractDao {

  public Group insert(Group group){
    if (group.isNew()){
      int id = insertGeneratedId(group);
      group.setId(id);
    }else {
      insertWithId(group);
    }
    return group;
  }

  @SqlQuery("SELECT nextval('general_seq')")
  abstract int getNextVal();

  @Transaction
  public int getSeqAndSkip(int step) {
    int id = getNextVal();
    DBIProvider.getDBI().useHandle(h -> h.execute("ALTER SEQUENCE general_seq RESTART WITH " + (id + step)));
    return id;
  }

  @SqlUpdate("TRUNCATE groups CASCADE")
  @Override
  public abstract void clean();


  @SqlQuery("SELECT * FROM groups ORDER BY name LIMIT :it")
  public abstract List<Group> getWithLimit(@Bind int limit);

  @SqlQuery("SELECT * FROM groups WHERE project_id = :it")
  public abstract List<Group> getByProjectId(@Bind int projectId);

  @SqlUpdate("INSERT INTO groups (name, group_type, project_id) VALUES (:name, CAST(:groupType AS GROUP_TYPE), :projectId) ")
  @GetGeneratedKeys
  abstract int insertGeneratedId(@BindBean Group group);

  @SqlUpdate("INSERT INTO groups (id, name, group_type, project_id) VALUES (:id, :name, CAST(:groupType AS GROUP_TYPE), :projectId) ")
  abstract void insertWithId(@BindBean Group group);

  @SqlBatch("INSERT INTO groups (id, name, group_type, project_id) VALUES (:id, :name, CAST(:groupType AS GROUP_TYPE), :it) " +
      "ON CONFLICT DO NOTHING")
  public abstract int[] insertBatchForProject(@BindBean List<Group> groups, @Bind int projectId, @BatchChunkSize int chunkSize);
}
