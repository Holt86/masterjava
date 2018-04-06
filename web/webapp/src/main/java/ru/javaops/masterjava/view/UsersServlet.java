package ru.javaops.masterjava.view;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;

@WebServlet(urlPatterns = "/", loadOnStartup = 1)
public class UsersServlet extends HttpServlet {

    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        webContext.setVariable("users", userDao.getWithLimit(20));
        engine.process("result", webContext, resp.getWriter());
    }
}
