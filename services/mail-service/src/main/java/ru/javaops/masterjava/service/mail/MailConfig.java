package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
import lombok.Getter;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import ru.javaops.masterjava.config.Configs;

/**
 * Created by user on 21.09.2018.
 */
@Getter
public class MailConfig {

  private static final MailConfig INSTANCE = new MailConfig(Configs.getConfig("mail.conf", "mail"));

  private final String HOST_NAME;
  private final int SMTP_PORT;
  private final String USER_NAME;
  private final String PASSWORD;
  private final boolean USE_SSL;
  private final boolean USE_TLS;
  private final boolean DEBUG;
  private final String FROM_EMAIL;

  public MailConfig(Config config) {
    HOST_NAME = config.getString("host");
    SMTP_PORT = config.getNumber("port").intValue();
    USER_NAME = config.getString("username");
    PASSWORD = config.getString("password");
    USE_SSL = config.getBoolean("useSSL");
    USE_TLS = config.getBoolean("useTLS");
    DEBUG = config.getBoolean("debug");
    FROM_EMAIL = config.getString("fromName");
  }

  public static MailConfig getInstance() {
    return INSTANCE;
  }

  public <T extends Email> T getEmail(T email){
    email.setHostName(HOST_NAME);
    email.setSmtpPort(SMTP_PORT);
    email.setAuthenticator(new DefaultAuthenticator(USER_NAME, PASSWORD));
    email.setSSLOnConnect(USE_SSL);
    email.setStartTLSEnabled(USE_TLS);
    email.setDebug(DEBUG);
    return email;
  }

  public HtmlEmail getHtmlEmail(HtmlEmail email){
    return getEmail(new HtmlEmail());
  }


}
