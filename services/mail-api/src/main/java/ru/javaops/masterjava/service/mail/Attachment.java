package ru.javaops.masterjava.service.mail;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by user on 02.11.2018.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Attachment {
  @XmlAttribute
  private String name;
  @XmlValue
  @XmlMimeType("application/octet-stream")
  private DataHandler file;
}
