package ru.javaops.masterjava.service.mail.handler;

import static ru.javaops.masterjava.service.mail.MailWSClient.*;

import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.web.WsClient;
import ru.javaops.masterjava.web.handler.SoapLoggingHandlers;

/**
 * Created by user on 12.11.2018.
 */
public class MailSoapServerLoggingHandler extends SoapLoggingHandlers {
  private static final String SERVER = "server";

  public MailSoapServerLoggingHandler() {
    super(WsClient.getLoggerLevel(MAIL_CONFIG, SERVER));
  }

  @Override
  protected boolean isRequest(boolean isOutbound) {
    return !isOutbound;
  }
}
