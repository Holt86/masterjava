package ru.javaops.masterjava.persist.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.model.Project;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class ProjectDao implements AbstractDao {

  public Project insert(Project project){
    if (project.isNew()){
      int id = insertGeneratedId(project);
      project.setId(id);
    }else {
      insertWithId(project);
    }
    return project;
  }

  @SqlUpdate("TRUNCATE projects CASCADE")
  @Override
  public abstract void clean();


  @SqlQuery("SELECT * FROM projects ORDER BY name LIMIT :it")
  public abstract List<Project> getWithLimit(@Bind int limit);

  @SqlUpdate("INSERT INTO projects (name, description) VALUES (:name, :description) ")
  @GetGeneratedKeys
  abstract int insertGeneratedId(@BindBean Project project);

  @SqlUpdate("INSERT INTO projects (id, name, description) VALUES (:id, :name, :description) ")
  abstract void insertWithId(@BindBean Project project);
}
