package ru.javaops.masterjava.service.mail.jms;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.javaops.masterjava.service.mail.Addressee;

/**
 * Created by user on 26.11.2018.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JmsEmail implements Serializable{
  private String users;
  private String subject;
  private String body;
  private JmsAttach attach;
}
