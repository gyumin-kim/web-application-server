package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private RequestLine requestLine;
	private Map<String, String> parameters = new HashMap<>();
	private final Map<String, String> headers = new HashMap<>();

	public HttpRequest(final InputStream in) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			String line = bufferedReader.readLine();
			if (line == null) {
				return;
			}
			requestLine = new RequestLine(line);

			line = bufferedReader.readLine();
			while (!line.equals("")) {
				log.debug("line: {}", line);
				HttpRequestUtils.Pair header = HttpRequestUtils.parseHeader(line);
				headers.put(header.getKey(), header.getValue());
				line = bufferedReader.readLine();
			}
			if (requestLine.getMethod().isPost()) {
				String contentLength = headers.get("Content-Length");
				String body = IOUtils.readData(bufferedReader, Integer.parseInt(contentLength));
				parameters = HttpRequestUtils.parseQueryString(body);
			} else {
				parameters = requestLine.getParameters();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public HttpMethod getMethod() {
		return this.requestLine.getMethod();
	}

	public String getPath() {
		return this.requestLine.getPath();
	}

	public String getHeader(final String header) {
		return this.headers.get(header);
	}

	public String getParameter(final String parameter) {
		return this.parameters.get(parameter);
	}

	public HttpCookie getCookies() {
		return new HttpCookie(getHeader("Cookie"));
	}

	public boolean isLogin() {
		String cookies = this.getHeader("Cookie");
		Map<String, String> cookie = HttpRequestUtils.parseCookies(cookies);
		String value = cookie.get("logined");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

//	public HttpSession getSession() {
//		return null;
//	}
}
