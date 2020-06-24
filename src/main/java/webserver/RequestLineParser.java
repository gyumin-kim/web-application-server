package webserver;

public class RequestLineParser {

	public static String parseUrl(final String line) {
		String[] tokens = line.split(" ");
		if (tokens[0].equals("GET")) {
			return tokens[1];
		}
		return "";
	}
}
