package ru.javaops.masterjava.service.mail.listeners;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.jms.JmsEmail;
import ru.javaops.masterjava.web.WebStateException;

@WebListener
@Slf4j
public class JmsMailListener implements ServletContextListener {

  private Thread listenerThread = null;
  private QueueConnection connection;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      InitialContext initCtx = new InitialContext();
      QueueConnectionFactory connectionFactory =
          (QueueConnectionFactory) initCtx.lookup("java:comp/env/jms/ConnectionFactory");
      ActiveMQConnectionFactory cf = (ActiveMQConnectionFactory) connectionFactory;
      cf.setTrustedPackages(Arrays.asList("ru.javaops.masterjava.service.mail"));
      connection = connectionFactory.createQueueConnection();
      QueueSession queueSession = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
      Queue queue = (Queue) initCtx.lookup("java:comp/env/jms/queue/MailQueue");
      QueueReceiver receiver = queueSession.createReceiver(queue);
      connection.start();
      log.info("Listen JMS messages ...");
      listenerThread = new Thread(() -> {
        try {
          while (!Thread.interrupted()) {
            Message m = receiver.receive();
            // TODO implement mail sending
            if (m instanceof TextMessage) {
              TextMessage tm = (TextMessage) m;
              String text = tm.getText();
              log.info("Received TextMessage with text '{}'", text);
            } else if (m instanceof ObjectMessage) {
              ObjectMessage om = (ObjectMessage) m;
              JmsEmail email = (JmsEmail) om.getObject();
              log.info("Received TextMessage with text '{}'", email);
              Attachment attachment = null;
              if (email.getAttach() != null) {
                attachment = new Attachment(email.getAttach().getFileName(),
                    new DataHandler(new ByteArrayDataSource(email.getAttach().getBytes(),
                        email.getAttach().getContentType())));
              }
              try {
                MailServiceExecutor
                    .sendBulk(MailWSClient.split(email.getUsers()), email.getSubject(),
                        email.getBody(),
                        attachment == null ? Collections.emptyList() :
                            ImmutableList.of(attachment));
              }catch (WebStateException e){
                log.warn("Send email fail with cause " + e.getFaultInfo());
              }
            }
          }
        } catch (Exception e) {
          log.error("Receiving messages failed: " + e.getMessage(), e);
        }
      });
      listenerThread.start();
    } catch (Exception e) {
      log.error("JMS failed: " + e.getMessage(), e);
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    if (connection != null) {
      try {
        connection.close();
      } catch (JMSException ex) {
        log.warn("Couldn't close JMSConnection: ", ex);
      }
    }
    if (listenerThread != null) {
      listenerThread.interrupt();
    }
  }
}