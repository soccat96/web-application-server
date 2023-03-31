package controller;

import enums.HttpMethod;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;

abstract public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();

        if (method.isGet()) {
            doGet(request, response);
        }
        if (method.isPost()) {
            doPost(request, response);
        }
    }

    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
    }

    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
    }
}
