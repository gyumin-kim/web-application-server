package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            while (!line.equals("")) {
                line = bufferedReader.readLine();
                log.debug("line: {}", line);
                HttpRequestUtils.Pair header = HttpRequestUtils.parseHeader(line);
                if (header != null && header.getKey().equals("Content-Length")) {
                    contentLength = Integer.parseInt(header.getValue());
                }
            }
            DataOutputStream dos = new DataOutputStream(out);
            if (requestPath.equals("/user/create")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> parameters = HttpRequestUtils.parseQueryString(data);
                String userId = parameters.get("userId");
                String password = parameters.get("password");
                String name = parameters.get("name");
                String email = parameters.get("email");
                User user = new User(userId, password, name, email);
                log.debug("User 객체 생성: {}", user);
                DataBase.addUser(user);
                byte[] body = getBody("/");
                response302Header(dos, "/");
                responseBody(dos, body);
                return;
            } else if (requestPath.equals("/user/login")) {
                String data = IOUtils.readData(bufferedReader, contentLength);
                Map<String, String> parameters = HttpRequestUtils.parseQueryString(data);
                String userId = parameters.get("userId");
                String password = parameters.get("password");
                User userById = DataBase.findUserById(userId);
                String path = "";
                if (userById.getPassword().equals(password)) {
                    path =  "/";
                    byte[] body = getBody(path);
                    response200WithLoginHeader(dos, true);
                    responseBody(dos, body);
                } else {
                    path = "/user/login_failed.html";
                    byte[] body = getBody(path);
                    response200WithLoginHeader(dos, false);
                    responseBody(dos, body);
                }
                return;
            }
            byte[] body = getBody(requestPath);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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

    private void response200WithLoginHeader(DataOutputStream dos, boolean logined) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=" + logined + "\r\n");
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
