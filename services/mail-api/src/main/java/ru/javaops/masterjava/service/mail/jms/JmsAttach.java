package ru.javaops.masterjava.service.mail.jms;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by user on 26.11.2018.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JmsAttach implements Serializable {
  private byte[] bytes;
  private String fileName;
  private String contentType;

}
