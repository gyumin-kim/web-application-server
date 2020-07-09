package webserver;

import http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
	private final String testDirectory = "./src/test/resources/";

	@DisplayName("GET 요청의 HTTP method, path, header, parameter 정보를 읽어들인다")
	@Test
	void request_GET() throws IOException {
		InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
		HttpRequest request = new HttpRequest(in);

		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/user/create");
		assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
		assertThat(request.getParameter("userId")).isEqualTo("javajigi");
	}
}