package ru.javaops.masterjava.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;


@WebServlet(urlPatterns = "/upload", name = "upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    final Part filePart = req.getPart("file");
    final List<User> users = new ArrayList<>();

    try (InputStream is = filePart.getInputStream()) {
      StaxStreamProcessor processor = new StaxStreamProcessor(is);
      while (processor.startElement("User", "Users")) {
        User user = new User();
        user.setEmail(processor.getAttribute("email"));
        user.setFlag(FlagType.fromValue(processor.getAttribute("flag")));
        user.setValue(processor.getText());
        users.add(user);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    req.setAttribute("users", users);
    req.getRequestDispatcher("/WEB-INF/upload.jsp").forward(req, resp);
  }
}
