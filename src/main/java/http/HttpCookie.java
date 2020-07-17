package http;

import util.HttpRequestUtils;

import java.util.Map;

public class HttpCookie {

	private final Map<String, String> cookies;

	public HttpCookie(final String cookieValue) {
		this.cookies = HttpRequestUtils.parseCookies(cookieValue);
	}

	public String get(String name) {
		return cookies.get(name);
	}
}
