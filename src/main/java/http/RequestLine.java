package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

	private final HttpMethod method;
	private final String path;
	private Map<String, String> parameters = new HashMap<>();

	public RequestLine(final String requestLine) {
		log.debug("request line: {}", requestLine);
		method = HttpMethod.valueOf(RequestLineParser.extractHttpMethod(requestLine));
		String url = RequestLineParser.extractUrl(requestLine);
		if (method.isGet()) {
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

	public Map<String, String> getParameters() {
		return this.parameters;
	}

	public HttpMethod getMethod() {
		return this.method;
	}

	public String getPath() {
		return this.path;
	}
}
