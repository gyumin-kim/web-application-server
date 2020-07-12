package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpResponse {

	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	private final DataOutputStream dos;
	private final Map<String, String> headers = new HashMap<>();

	public HttpResponse(final OutputStream out) {
		dos = new DataOutputStream(out);
	}

	public void sendRedirect(final String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found \r\n");
			writeHeaders();
			dos.writeBytes("Location: " + url + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void writeHeaders() {
		Set<String> keys = headers.keySet();
		for (String key : keys) {
			try {
				dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	public void addHeader(final String key, final String value) {
		headers.put(key, value);
	}

	public void forwardBody(final String body) {
		byte[] contents = body.getBytes();
		headers.put("Content-Type", "text/html;charset=utf-8");
		headers.put("Content-Length", String.valueOf(contents.length));
		response200Header();
		responseBody(contents);
	}

	public void forward(final String url) {
		try {
			byte[] body = Files.readAllBytes(new File(getPathName(url)).toPath());
			if (url.endsWith(".css")) {
				headers.put("Content-Type", "text/css");
			} else if (url.endsWith(".js")) {
				headers.put("Content-Type", "application/javascript");
			} else {
				headers.put("Content-Type", "text/html;charset=utf-8");
			}
			headers.put("Content-Length: ", String.valueOf(body.length));
			response200Header();
			responseBody(body);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private String getPathName(final String url) {
		if (url == null || url.equals("/")) {
			return "./webapp/index.html";
		}
		return "./webapp" + url;
	}

	private void response200Header() {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			writeHeaders();
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(final byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
