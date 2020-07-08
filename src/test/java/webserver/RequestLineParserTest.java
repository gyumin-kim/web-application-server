package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineParserTest {

	@DisplayName("요청 라인에서 HTTP 메서드를 추출한다")
	@Test
	void parseHttpMethod() {
		// given
		String requestLine = "GET /index.html HTTP/1.1";

		// when
		String method = RequestLineParser.extractHttpMethod(requestLine);

		// then
		assertThat(method).isEqualTo("GET");
	}

	@DisplayName("요청 라인에서 URL을 추출한다")
	@Test
	void parseUrl() {
		// given
		String requestLine = "GET /index.html HTTP/1.1";

		// when
		String url = RequestLineParser.extractUrl(requestLine);

		// then
		assertThat(url).isEqualTo("/index.html");
	}

	@DisplayName("요청 라인의 URL에서 request path를 추출한다")
	@Test
	void parseRequestPath() {
		// given
		String url = "/user/create?userId=testId&password=1234&name=testName&email=test@email.com";

		// when
		String requestPath = RequestLineParser.extractRequestPath(url);

		// then
		assertThat(requestPath).isEqualTo("/user/create");
	}

	@DisplayName("요청 라인의 URL에 '?'가 없으면 추출된 request path는 url과 동일")
	@Test
	void parseRequestPathWithoutQuestionMark() {
		// given
		String url = "/user/create";

		// when
		String requestPath = RequestLineParser.extractRequestPath(url);

		// then
		assertThat(requestPath).isEqualTo(url);
	}

	@DisplayName("요청 라인의 URL에서 query string을 추출한다")
	@Test
	void parseQueryString() {
		// given
		String url = "/user/create?userId=testId&password=1234&name=testName&email=test@email.com";

		// when
		String queryString = RequestLineParser.extractQueryString(url);

		// then
		assertThat(queryString).isEqualTo("userId=testId&password=1234&name=testName&email=test@email.com");
	}

	@DisplayName("요청 라인의 URL에 '?'가 없으면 추출된 query string은 빈 문자열")
	@Test
	void parseQueryStringWithoutQuestionMark() {
		// given
		String url = "/user/create";

		// when
		String queryString = RequestLineParser.extractQueryString(url);

		// then
		assertThat(queryString).isEmpty();
	}
}