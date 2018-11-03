package ru.javaops.masterjava.service.mail;

import java.util.List;
import java.util.Set;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import ru.javaops.web.WebStateException;

@WebService(targetNamespace = "http://mail.javaops.ru/")
//@SOAPBinding(
//        style = SOAPBinding.Style.DOCUMENT,
//        use= SOAPBinding.Use.LITERAL,
//        parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface MailService {

  @WebMethod
  String sendToGroup(
      @WebParam(name = "to") Set<Addressee> to,
      @WebParam(name = "cc") Set<Addressee> cc,
      @WebParam(name = "subject") String subject,
      @WebParam(name = "body") String body,
      @WebParam(name = "attachment") List<Attachment> attachments) throws WebStateException;

  @WebMethod
  GroupResult sendBulk(
      @WebParam(name = "to") Set<Addressee> to,
      @WebParam(name = "subject") String subject,
      @WebParam(name = "body") String body,
      @WebParam(name = "attachment") List<Attachment> attachments) throws WebStateException;

}