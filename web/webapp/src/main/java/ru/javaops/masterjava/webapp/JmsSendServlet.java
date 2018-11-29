package ru.javaops.masterjava.webapp;

import static org.apache.commons.io.IOUtils.toByteArray;

import java.io.IOException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.jms.JmsAttach;
import ru.javaops.masterjava.service.mail.jms.JmsEmail;

@WebServlet("/sendJms")
@Slf4j
@MultipartConfig
public class JmsSendServlet extends HttpServlet {

  private Connection connection;
  private Session session;
  private MessageProducer producer;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    try {
      InitialContext initCtx = new InitialContext();
      ConnectionFactory connectionFactory = (ConnectionFactory) initCtx
          .lookup("java:comp/env/jms/ConnectionFactory");
      connection = connectionFactory.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      producer = session
          .createProducer((Destination) initCtx.lookup("java:comp/env/jms/queue/MailQueue"));
    } catch (Exception e) {
      throw new IllegalStateException("JMS init failed", e);
    }
  }

    @Override
    public void destroy() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                log.warn("Couldn't close JMSConnection: ", ex);
            }
        }
    }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String result;
    try {
      log.info("Start sending");
      req.setCharacterEncoding("UTF-8");
      resp.setCharacterEncoding("UTF-8");
      String users = req.getParameter("users");
      String subject = req.getParameter("subject");
      String body = req.getParameter("body");
      Part filePart = req.getPart("attach");
      JmsEmail email = new JmsEmail(users, subject, body, new JmsAttach(
          toByteArray(filePart.getInputStream()), filePart.getSubmittedFileName(),
          filePart.getContentType()));
      result = sendJms(email);
      log.info("Processing finished with result: {}", result);
    } catch (Exception e) {
      log.error("Processing failed", e);
      result = e.toString();
    }
    resp.getWriter().write(result);
  }

  private synchronized String sendJms(JmsEmail email) throws JMSException {
    ObjectMessage message = session.createObjectMessage();
    message.setObject(email);
    producer.send(message);
    return "Successfully sent JMS message";
  }
}