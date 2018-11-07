package ru.javaops.masterjava.service.mail;


import com.sun.xml.ws.developer.StreamingAttachment;
import java.util.List;
import javax.activation.DataHandler;
import javax.xml.ws.soap.MTOM;
import ru.javaops.web.WebStateException;

import javax.jws.WebService;
import java.util.Set;

@WebService(endpointInterface = "ru.javaops.masterjava.service.mail.MailService", targetNamespace = "http://mail.javaops.ru/"
//          , wsdlLocation = "WEB-INF/wsdl/mailService.wsdl"
)
@MTOM
@StreamingAttachment(parseEagerly = true, memoryThreshold = 1024)
public class MailServiceImpl implements MailService {

  public String sendToGroup(Set<Addressee> to, Set<Addressee> cc, String subject, String body,
      List<Attachment> attachments) throws WebStateException {
    System.out.println(attachments.size());
    return MailSender.sendToGroup(to, cc, subject, body, attachments);
  }

  @Override
  public GroupResult sendBulk(Set<Addressee> to, String subject, String body,
      List<Attachment> attachments) throws WebStateException {
    System.out.println(attachments.size());
    return MailServiceExecutor.sendBulk(to, subject, body, attachments);
  }
}