package ru.javaops.masterjava.webapp;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.thymeleaf.context.WebContext;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import ru.javaops.masterjava.service.mail.Addressee;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailWSClient;

import static ru.javaops.masterjava.common.web.ThymeleafListener.engine;

@WebServlet("")
public class UsersServlet extends HttpServlet {
    private UserDao userDao = DBIProvider.getDao(UserDao.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale(),
                ImmutableMap.of("users", userDao.getWithLimit(20)));
        engine.process("users", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        String[] users = req.getParameter("users").split(",");
        Set<Addressee> addressees = Arrays.stream(users).map(Addressee::new)
            .collect(Collectors.toSet());
        String subject = req.getParameter("subject");
        String body = req.getParameter("body");
        GroupResult groupResult = MailWSClient.sendBulk(addressees, subject, body);
        final WebContext webContext = new WebContext(req, resp, req.getServletContext(), req.getLocale());
        webContext.setVariable("groupResult", groupResult);
        engine.process("result", webContext, resp.getWriter());

    }
}
