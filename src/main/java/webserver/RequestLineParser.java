package webserver;

public class RequestLineParser {

	public static String parseUrl(final String line) {
		String[] tokens = line.split(" ");
		return tokens[1];
	}

	public static String parseRequestPath(final String url) {
		if (!url.contains("?")) {
			return url;
		}
		int index = url.indexOf("?");
		return url.substring(0, index);
	}

	public static String parseQueryString(final String url) {
		if (!url.contains("?")) {
			return "";
		}
		int index = url.indexOf("?");
		return url.substring(index + 1);
	}
}
