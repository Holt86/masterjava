package ru.javaops.masterjava.webapp;

import static com.google.common.io.ByteStreams.toByteArray;

import com.sun.xml.ws.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;

@WebServlet("/send")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10)
@Slf4j
public class SendServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String result;
    List<Attachment> attachments = new ArrayList<>();
    try {
      log.info("Start sending");
      req.setCharacterEncoding("UTF-8");
      resp.setCharacterEncoding("UTF-8");
      String users = req.getParameter("users");
      String subject = req.getParameter("subject");
      String body = req.getParameter("body");

      List<Part> parts = req.getParts().stream().filter(
          e -> e.getSubmittedFileName() != null).collect(Collectors.toList());
      if (!parts.isEmpty()) {
        for (Part part : parts) {
          attachments.add(new Attachment(part.getSubmittedFileName(),
              new DataHandler(new ByteArrayDataSource(toByteArray(part.getInputStream()),
                  part.getContentType()))));
        }
      }
      GroupResult groupResult = MailWSClient
          .sendBulk(MailWSClient.split(users), subject, body, attachments);
      result = groupResult.toString();
      log.info("Processing finished with result: {}", result);
    } catch (Exception e) {
      log.error("Processing failed", e);
      result = e.toString();
    }
    resp.getWriter().write(result);
  }
}
