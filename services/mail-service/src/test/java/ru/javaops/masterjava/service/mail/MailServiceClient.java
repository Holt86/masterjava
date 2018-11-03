package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.web.WebStateException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;

public class MailServiceClient {

  public static void main(String[] args) throws MalformedURLException, WebStateException {
    Service service = Service.create(
        new URL("http://localhost:8080/mail/mailService?wsdl"),
        new QName("http://mail.javaops.ru/", "MailServiceImplService"));

    MailService mailService = service.getPort(MailService.class);

    FileDataSource dataSource1 = new FileDataSource(Configs.getConfigFile("files/лайки.txt"));
    FileDataSource dataSource2 = new FileDataSource(Configs.getConfigFile("files/скан.PDF"));
    List<Attachment> attachments = Arrays
        .asList(new Attachment("лайки.txt", new DataHandler(dataSource1)),
            new Attachment("скан.PDF", new DataHandler(dataSource2)));

    String state = mailService
        .sendToGroup(ImmutableSet.of(new Addressee("aleksandr_986@inbox.ru", null)), null,
            "Group mail subject", "Group mail body",
            attachments);
    System.out.println("Group mail state: " + state);

    GroupResult groupResult = mailService.sendBulk(ImmutableSet.of(
        new Addressee("Мастер User <aleksandr_986@inbox.ru>"),
        new Addressee("Bad Email <bad_email.ru>")), "Bulk mail subject", "Bulk mail body",
        attachments);
    System.out.println("\nBulk mail groupResult:\n" + groupResult);
  }
}
