package controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ListUserController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(ListUserController.class);

	@Override
	protected void doGet(final HttpRequest request, final HttpResponse response) {
		if (!request.isLogin()) {
			response.sendRedirect("/user/login.html");
			return;
		}
		Collection<User> users = DataBase.findAll();
		long id = 1;
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='1'>");
		for (User user : users) {
			sb.append("<sb>");
			sb.append("<th scope=\"row\">").append(id++).append("</th>");
			sb.append("<td>").append(user.getUserId()).append("</td>");
			sb.append("<td>").append(user.getName()).append("</td>");
			sb.append("<td>").append(user.getEmail()).append("</td>");
			sb.append("</sb>");
		}
		sb.append("</table>");
		response.forwardBody(sb.toString());
	}
}
