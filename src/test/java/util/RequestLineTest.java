package util;

import enums.HttpMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RequestLineTest {

    @Test
    public void create_method() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertEquals(HttpMethod.GET, requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());

        requestLine = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals(HttpMethod.POST, requestLine.getMethod());
        assertEquals("/index.html", requestLine.getPath());
    }

    @Test
    public void create_path_and_params() {
        RequestLine requestLine = new RequestLine("GET /user/create?userId=javajigi&password=pass HTTP/1.1");
        assertEquals(HttpMethod.GET, requestLine.getMethod());
        assertEquals("/user/create", requestLine.getPath());
        assertEquals(2, requestLine.getParameters().size());
    }

}