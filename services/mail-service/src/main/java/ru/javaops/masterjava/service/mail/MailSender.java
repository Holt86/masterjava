package ru.javaops.masterjava.service.mail;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import com.typesafe.config.Config;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.service.mail.dao.EmailSendDao;
import ru.javaops.masterjava.service.mail.model.EmailSend;

@Slf4j
public class MailSender {

  private static EmailSendDao emailSendDao = DBIProvider.getDao(EmailSendDao.class);

  static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
    MailConfig config = MailConfig.getInstance();
    log.info(
        "Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled()
            ? "\nbody=" + body : ""));
    to = validate(to);
    cc = validate(cc);
    if (nonNull(to) && !to.isEmpty()) {
      EmailSend emailSend = new EmailSend(config.getFROM_EMAIL(), getEmailListHowString(to),
          getEmailListHowString(cc), subject, body);
      try {
        Email email = config.getEmail(new SimpleEmail());
        email.setFrom(config.getFROM_EMAIL());
        email.setSubject(subject);
        email.setMsg(body);
        email.addTo(to.stream().map(Addressee::getEmail).toArray(String[]::new));
        if (nonNull(cc) && !cc.isEmpty()) {
          email.addTo(cc.stream().map(Addressee::getEmail).toArray(String[]::new));
          emailSend.setCcList(getEmailListHowString(cc));
        }
        email.send();
        emailSend.setSuccess(true);
      } catch (EmailException e) {
        emailSend.setSuccess(false);
        emailSend.setCause(e.getMessage());
        log.error(
            "ERROR \"Send mail to \\'\" + to + \"\\' cc \\'\" + cc + \"\\' subject \\'\" + subject + (log.isDebugEnabled()\n"
                + "            ? \"\\nbody=\" + body : \"\")");
      }
      emailSendDao.insertBatch(emailSend);
    } else {
      throw new IllegalArgumentException("Invalid data to email list");
    }
  }

  private static String getEmailListHowString(List<Addressee> addressees) {
    if (isNull(addressees)) {
      return "";
    }
    return addressees.stream()
        .map((e) -> isNull(e.getEmail()) ? null : e.getEmail())
        .collect(Collectors.joining(", "));
  }

  private static List<Addressee> validate(List<Addressee> addressee) {
    return isNull(addressee) ? null : addressee.stream().filter(Objects::nonNull).collect(toList());
  }
}
