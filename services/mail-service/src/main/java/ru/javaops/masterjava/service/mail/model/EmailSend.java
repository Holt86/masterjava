package ru.javaops.masterjava.service.mail.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import java.util.Date;
import lombok.*;

/**
 * Created by user on 16.09.2018.
 */
@Data
@NoArgsConstructor
public class EmailSend {

  private Date date;
  @Column("from_email")
  private @NonNull String from;
  @Column("to_list")
  private @NonNull String toList;
  @Column("cc_list")
  private String ccList;
  private String subject;
  private String message;
  private @NonNull boolean success;
  private String cause;

  public EmailSend(@NonNull String from, @NonNull String toList, String ccList, String subject,
      String message) {
    this.from = from;
    this.toList = toList;
    this.ccList = ccList;
    this.subject = subject;
    this.message = message;
  }
}
