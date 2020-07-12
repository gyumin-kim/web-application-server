package webserver;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            String requestPath = request.getPath();
            if (requestPath.equals("/user/create")) {
                String userId = request.getParameter("userId");
                String password = request.getParameter("password");
                String name = request.getParameter("name");
                String email = request.getParameter("email");
                User user = new User(userId, password, name, email);
                DataBase.addUser(user);
                response.sendRedirect("/index.html");
            } else if (requestPath.equals("/user/login")) {
                String userId = request.getParameter("userId");
                String password = request.getParameter("password");
                User userById = DataBase.findUserById(userId);
                if (userById == null) {
                    response.sendRedirect("/user/login_failed.html");
                    return;
                }
                if (userById.getPassword().equals(password)) {
                    response.addHeader("Set-Cookie", "logined=true");
                    response.sendRedirect("/index.html");
                    return;
                }
                response.sendRedirect("/user/login_failed.html");
            } else if (requestPath.equals("/user/list")) {
                if (!request.isLogin()) {
                    response.sendRedirect("/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                long id = 1;
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<sb>");
                    sb.append("<th scope=\"row\">").append(id++).append("</th>");
                    sb.append("<td>").append(user.getUserId()).append("</td>");
                    sb.append("<td>").append(user.getName()).append("</td>");
                    sb.append("<td>").append(user.getEmail()).append("</td>");
                    sb.append("</sb>");
                }
                sb.append("</table>");
                response.forwardBody(sb.toString());
            } else {
                response.forward(requestPath);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
