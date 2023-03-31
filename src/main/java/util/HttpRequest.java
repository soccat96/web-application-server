package util;

import enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private RequestLine requestLine;

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = br.readLine();

            if(line == null) return;

            requestLine = new RequestLine(line);

            if(requestLine.getMethod() == HttpMethod.POST) {
                parameters = HttpRequestUtils.parseQueryString(
                        IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")))
                );
            } else {
                parameters = requestLine.getParameters();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpMethod getMethod() {
        return this.requestLine.getMethod();
    }
    public String getPath() {
        return this.requestLine.getPath();
    }
    public String getHeader(String header) {
        return this.headers.get(header);
    }
    public String getParameter(String parameter) {
        return this.parameters.get(parameter);
    }

    public boolean isCookieBoolean(String cookieName) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(this.headers.get("Cookie"));
        if(cookies.containsKey(cookieName)) {
            return false;
        }

        return Boolean.parseBoolean(cookies.get(cookieName));
    }
}
