package controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

public abstract class AbstractController implements Controller {

	@Override
	public void service(final HttpRequest request, final HttpResponse response) {
		HttpMethod method = request.getMethod();
		if (method.isGet()) {
			doGet(request, response);
			return;
		}
		doPost(request, response);
	}

	protected void doGet(final HttpRequest request, final HttpResponse response) {}

	protected void doPost(final HttpRequest request, final HttpResponse response) {}
}
