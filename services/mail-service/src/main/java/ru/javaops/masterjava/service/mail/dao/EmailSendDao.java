package ru.javaops.masterjava.service.mail.dao;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.dao.AbstractDao;
import ru.javaops.masterjava.service.mail.model.EmailSend;

/**
 * Created by user on 16.09.2018.
 */
@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class EmailSendDao implements AbstractDao {

  @SqlUpdate("TRUNCATE sender CASCADE")
  @Override
  public abstract void clean();

  @SqlUpdate("INSERT INTO sender (from_email, to_list, cc_list, subject, message, success, cause) VALUES "
      + "(:from, :toList, :ccList, :subject, :message, :success, :cause)")
  public abstract void insertBatch(@BindBean EmailSend emailSends);


}
