package util;

import enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private HttpMethod method;
    private String path;
    private Map<String, String> parameters = new HashMap<>();

    public RequestLine(String requestLine) {
        String[] split = requestLine.split(" ");
        if(split.length != 3) {
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }
        this.method = HttpMethod.valueOf(split[0]);
        if(this.method.isPost()) {
            this.path = split[1];
            return ;
        }

        int idx = split[1].indexOf("?");
        if(idx == -1) {
            this.path = split[1];
        } else {
            this.path = split[1].substring(0, idx);
            this.parameters = HttpRequestUtils.parseQueryString(split[1].substring(idx + 1));
        }
    }

    public HttpMethod getMethod() {
        return this.method;
    }
    public String getPath() {
        return this.path;
    }
    public Map<String, String> getParameters() {
        return this.parameters;
    }
}
