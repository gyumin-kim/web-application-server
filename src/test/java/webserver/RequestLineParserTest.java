package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

	@DisplayName("요청 라인에서 URL을 추출한다")
	@Test
	void parseUrl() {
		// given
		String requestLine = "GET /index.html HTTP/1.1";

		// when
		String url = RequestLineParser.parseUrl(requestLine);

		// then
		assertThat(url).isEqualTo("/index.html");
	}
}