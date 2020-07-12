package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

	@DisplayName("각 URL에 해당하는 Controller 구현체를 RequestMapping으로부터 가져온다")
	@Test
	void getController() {
		// when
		Controller createUserController = RequestMapping.getController("/user/create");
		Controller loginController = RequestMapping.getController("/user/login");
		Controller listUserController = RequestMapping.getController("/user/list");

		// then
		assertThat(createUserController).isInstanceOf(CreateUserController.class);
		assertThat(loginController).isInstanceOf(LoginController.class);
		assertThat(listUserController).isInstanceOf(ListUserController.class);
	}
}