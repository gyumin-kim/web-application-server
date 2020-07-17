package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

	private HttpSessions() {
	}

	private static final Map<String, HttpSession> sessions = new HashMap<>();

	public static HttpSession getSession(final String id) {
		HttpSession session = sessions.get(id);
		if (session == null) {
			session = new HttpSession(id);
			sessions.put(id, session);
			return session;
		}
		return session;
	}

	public static void remove(final String id) {
		sessions.remove(id);
	}
}
