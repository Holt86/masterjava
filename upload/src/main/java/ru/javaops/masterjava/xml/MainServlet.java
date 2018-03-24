package ru.javaops.masterjava.xml;

import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/", name = "main", loadOnStartup = 1)
public class MainServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setAttribute("users", Collections.EMPTY_LIST);
    req.getRequestDispatcher("/WEB-INF/upload.jsp").forward(req, resp);
  }
}
