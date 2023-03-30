package util;

import java.util.HashMap;

public class HttpRequest {
    private String method;
    private String path;
    private HashMap<String, String> parameter = new HashMap<>();
    private HashMap<String, String> header = new HashMap<>();

    public HttpRequest(String requestString) {
        String[] split = requestString.split("\n");

        String[] request = split[0].split(" ");
        this.method = request[0];

        if(this.method.equals("GET")) {
            String[] uri = request[1].split("\\?");
            this.path = uri[0];

            String[] params = uri[1].split("&");
            for(int i=0; i<params.length; i++) {
                String[] param = params[i].split("=");
                this.parameter.put(param[0], param[1]);
            }

            for(int i=1; i<split.length; i++) {
                String h = split[i];

                if(h == null || h.equals("")) break;

                int idx = h.indexOf(":");
                this.header.put(h.substring(0, idx), h.substring(idx + 1, h.length()).trim());

            }
        } else if(this.method.equals("POST")) {
            this.path = request[1];

            int line = 1;
            for(; line<split.length; line++) {
                String h = split[line];

                if(h == null || h.equals("")) {
                    line++;
                    break;
                }

                int idx = h.indexOf(":");
                this.header.put(h.substring(0, idx), h.substring(idx + 1, h.length()).trim());
            }

            String[] params = split[line].substring(0, Integer.parseInt(getHeader("Content-Length"))).split("&");
            for(int i=0; i<params.length; i++) {
                String[] param = params[i].split("=");
                this.parameter.put(param[0], param[1]);
            }
        }

    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getHeader(String header) {
        return this.header.get(header);
    }

    public String getParameter(String parameter) {
        return this.parameter.get(parameter);
    }
}
