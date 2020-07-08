package webserver;

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

	private String method;
	private String path;
	private Map<String, String> parameters = new HashMap<>();
	private final Map<String, String> headers = new HashMap<>();

	public HttpRequest(final InputStream in) {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			String line = bufferedReader.readLine();
			if (line == null) {
				return;
			}
			parseRequestLine(line);

			line = bufferedReader.readLine();
			while (!line.equals("")) {
				log.debug("line: {}", line);
				HttpRequestUtils.Pair header = HttpRequestUtils.parseHeader(line);
				headers.put(header.getKey(), header.getValue());
				line = bufferedReader.readLine();
			}
			if (method.equals("POST")) {
				String contentLength = headers.get("Content-Length");
				String body = IOUtils.readData(bufferedReader, Integer.parseInt(contentLength));
				parameters = HttpRequestUtils.parseQueryString(body);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void parseRequestLine(final String line) {
		method = RequestLineParser.extractHttpMethod(line);
		String url = RequestLineParser.extractUrl(line);
		if (method.equals("GET")) {
			if (!url.contains("?")) {
				path = url;
				return;
			}
			int index = url.indexOf("?");
			path = url.substring(0, index);
			parameters = HttpRequestUtils.parseQueryString(url.substring(index + 1));
			return;
		}
		path = url;
	}

	public String getMethod() {
		return this.method;
	}

	public String getPath() {
		return this.path;
	}

	public String getHeader(final String header) {
		return this.headers.get(header);
	}

	public String getParameter(final String parameter) {
		return this.parameters.get(parameter);
	}
}
