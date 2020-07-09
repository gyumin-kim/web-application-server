package webserver;

import http.HttpMethod;
import http.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

	@DisplayName("HTTP GET 요청의 method와 path를 가져오기")
	@Test
	void http_get_method_path() {
		// given
		RequestLine line = new RequestLine("GET /index.html HTTP/1.1");

		// when
		HttpMethod method = line.getMethod();
		String path = line.getPath();

		// then
		assertThat(method).isEqualTo(HttpMethod.GET);
		assertThat(path).isEqualTo("/index.html");
	}

	@DisplayName("HTTP POST 요청의 path를 가져오기")
	@Test
	void http_post_path() {
		// given
		RequestLine line = new RequestLine("POST /index.html HTTP/1.1");

		// when
		String path = line.getPath();

		// then
		assertThat(path).isEqualTo("/index.html");
	}

	@DisplayName("HTTP GET 요청(parameter 포함)")
	@Test
	void http_get_with_parameters() {
		// given
		RequestLine line = new RequestLine("GET /user/create?userId=javajigi&password=pass HTTP/1.1");

		// when
		HttpMethod method = line.getMethod();
		String path = line.getPath();
		Map<String, String> parameters = line.getParameters();

		// then
		assertThat(method).isEqualTo(HttpMethod.GET);
		assertThat(path).isEqualTo("/user/create");
		assertThat(parameters.size()).isEqualTo(2);
	}
}