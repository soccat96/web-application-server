package util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HttpRequestTest {
    @Test
    public void request_GET() {
        StringBuilder sb = new StringBuilder();
        sb.append("GET /user/create?userId=javajigi&password=password&name=JaeSung HTTP/1.1\n");
        sb.append("Host: localhost:8080\n");
        sb.append("Connection: Keep-alive\n");
        sb.append("Accept: */*\n");
        sb.append("\n");

        HttpRequest httpRequest = new HttpRequest(sb.toString());
        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("Keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("javajigi", httpRequest.getParameter("userId"));
    }

    @Test
    public void request_POST() {
        StringBuilder sb = new StringBuilder();
        sb.append("POST /user/create HTTP/1.1\n");
        sb.append("Host: localhost:8080\n");
        sb.append("Connection: keep-alive\n");
        sb.append("Content-Length: 46\n");
        sb.append("Content-Type: application/x-www-form-urlencoded\n");
        sb.append("Accept: */*\n");
        sb.append("\n");
        sb.append("userId=javajigi&password=password&name=JaeSung\n");

        HttpRequest httpRequest = new HttpRequest(sb.toString());
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("javajigi", httpRequest.getParameter("userId"));
    }
}