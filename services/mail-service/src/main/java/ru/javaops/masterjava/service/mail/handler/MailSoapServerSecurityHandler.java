package ru.javaops.masterjava.service.mail.handler;

import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.web.WsClient;
import ru.javaops.masterjava.web.handler.SoapServerSecurityHandler;

/**
 * Created by user on 12.11.2018.
 */
public class MailSoapServerSecurityHandler extends SoapServerSecurityHandler {

  public MailSoapServerSecurityHandler() {
    super(MailWSClient.getBaseEncodeCredentials());
  }
}
