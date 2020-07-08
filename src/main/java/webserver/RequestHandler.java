package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = bufferedReader.readLine();
            log.debug("line: {}", line);
            if (line == null) {
                return;
            }
            String url = RequestLineParser.extractUrl(line);
            String requestPath = RequestLineParser.extractRequestPath(url);
            int contentLength = 0;
            boolean logined = false;

            while (!line.equals("")) {
                line = bufferedReader.readLine();
                log.debug("line: {}", line);
                if (line.contains("Content-Length")) {
                    HttpRequestUtils.Pair header = HttpRequestUtils.parseHeader(line);
                    contentLength = Integer.parseInt(header.getValue());
                }
                if (line.contains("Cookie")) {
                    HttpRequestUtils.Pair header = HttpRequestUtils.parseHeader(line);
                    String cookies = header.getValue();
                    Map<String, String> cookie = HttpRequestUtils.parseCookies(cookies);
                    logined = Boolean.parseBoolean(cookie.get("logined"));
                }
            }
            if (requestPath.equals("/user/create")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> parameters = HttpRequestUtils.parseQueryString(data);
                String userId = parameters.get("userId");
                String password = parameters.get("password");
                String name = parameters.get("name");
                String email = parameters.get("email");
                User user = new User(userId, password, name, email);
                DataBase.addUser(user);
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html");
            } else if (requestPath.equals("/user/login")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> parameters = HttpRequestUtils.parseQueryString(data);
                String userId = parameters.get("userId");
                String password = parameters.get("password");
                User userById = DataBase.findUserById(userId);
                if (userById == null) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if (userById.getPassword().equals(password)) {
                    DataOutputStream dos = new DataOutputStream(out);
                    response302WithLoginHeader(dos);
                } else {
                    responseResource(out, "/user/login_failed.html");
                }
            } else if (requestPath.equals("/user/list")) {
                if (!logined) {
                    responseResource(out, "/user/login.html");
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
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if (requestPath.endsWith("css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = getBody(requestPath);
                response200HeaderCss(dos);
                responseBody(dos, body);
            } else {
                responseResource(out, url);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(final OutputStream out, final String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = getBody(url);
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private byte[] getBody(String url) throws IOException {
        String pathname = getPathName(url);
        return Files.readAllBytes(new File(pathname).toPath());
    }

    private String getPathName(final String url) {
        if (url.equals("/")) {
            return "./webapp/index.html";
        }
        return "./webapp" + url;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderCss(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302WithLoginHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
