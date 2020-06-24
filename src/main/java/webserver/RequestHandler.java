package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

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
            String requestUrl = RequestLineParser.parseUrl(line);
            while (!line.equals("")) {
                line = bufferedReader.readLine();
                log.debug("line: {}", line);
            }
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = getBody(requestUrl);
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
