package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;

public class ListUserController extends AbstractController {
    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isCookieBoolean("login")) {
            response.sendRedirect("/user/login.html");
            return;
        }

        ArrayList<User> users = new ArrayList<>(DataBase.findAll());
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (int i=0; i<users.size(); i++) {
            User user = users.get(i);
            sb.append("<tr>").append("<th scope=\"row\">").append(i + 1).append("</th>")
                    .append("<td>").append(user.getUserId()).append("</td>")
                    .append("<td>").append(user.getName()).append("</td>")
                    .append("<td>").append(user.getEmail()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }
}
