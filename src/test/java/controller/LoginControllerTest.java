package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

	@Mock
	private HttpRequest httpRequest;
	@Mock
	private HttpResponse httpResponse;

//	@InjectMocks
	private LoginController loginController;

	@BeforeEach
	void setUp() {
		loginController = new LoginController();
	}

	@DisplayName("")
	@Test
	void doPost() {
		// given
		User userStub = User.builder()
				.userId("testId")
				.password("password")
				.name("name")
				.email("test@email.com")
				.build();
		given(httpRequest.getParameter(anyString())).willReturn("password");
		given(DataBase.findUserById(anyString())).willReturn(userStub);	// TODO: Mockito는 static 메서드를 mocking할 수 없음!

		// when
		loginController.doPost(httpRequest, httpResponse);

		// then
		verify(httpResponse).sendRedirect("/index.html");
	}
}