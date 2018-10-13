package ru.javaops.masterjava.service.mail;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "groupResult", namespace = "http://mail.javaops.ru/")
public class GroupResult {

  @XmlAttribute(name = "success")
  private int success; // number of successfully sent email
  private List<MailResult> failed; // failed emails with causes
  @XmlAttribute(name = "rootCause")
  private String failedCause;  // global fail cause

  @Override
  public String toString() {
    return "Success: " + success + '\n' +
        (failed == null ? "" : "Failed: " + failed.toString() + '\n') +
        (failedCause == null ? "" : "Failed cause: " + failedCause);
  }
}