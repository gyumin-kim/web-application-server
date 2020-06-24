package webserver;

import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {
    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }

        // ServerSocket을 생성한다(ServerSocket; 사용자 요청이 발생할 때까지 대기 상태에 있도록 지원하는 역할).
        // 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            log.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될 때까지 대기한다. (Socket; 클라이언트와 연결을 담당)
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                // 사용자의 요청이 있을 경우, 요청을 RequestHandler 객체에 위임
                RequestHandler requestHandler = new RequestHandler(connection);
                requestHandler.start();
            }
        }
    }
}
