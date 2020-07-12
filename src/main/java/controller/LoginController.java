package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Override
	protected void doPost(final HttpRequest request, final HttpResponse response) {
		String userId = request.getParameter("userId");
		String password = request.getParameter("password");
		User userById = DataBase.findUserById(userId);
		if (userById == null) {
			response.sendRedirect("/user/login_failed.html");
			return;
		}
		if (userById.getPassword().equals(password)) {
			response.addHeader("Set-Cookie", "logined=true");
			response.sendRedirect("/index.html");
			return;
		}
		response.sendRedirect("/user/login_failed.html");
	}
}
