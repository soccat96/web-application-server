package util;

import com.google.common.io.Files;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;

public class HttpResponse {
    private DataOutputStream dos;
    private final HashMap<String, String> header = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void forward(String url) throws IOException {
        byte[] body = Files.toByteArray(new File("webapp" + url));

        if(url.endsWith(".css")) {
            addHeader("Content-Type", "text/css");
        } else if(url.endsWith(".js")) {
            addHeader("Content-Type", "*/*");
        } else {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }
        this.header.put("Content-Length", String.valueOf(body.length));
        response200Header(body.length);
        responseBody(body);
    }

    public void forwardBody(String body) throws IOException {
        byte[] contents = body.getBytes();
        this.header.put("Content-Type", "text/html;charset=utf-8");
        this.header.put("Content-Length", String.valueOf(contents.length));
        response200Header(contents.length);
        responseBody(contents);
    }

    public void sendRedirect(String url) throws IOException {
        this.dos.writeBytes("HTTP/1.1 302 Found" + System.lineSeparator());
        processHeaders();
        this.dos.writeBytes("Location: " + url + System.lineSeparator());
        this.dos.writeBytes(System.lineSeparator());
    }

    public void response200Header(int lengthOfBodyContent) throws IOException {
        this.dos.writeBytes("HTTP/1.1 200 OK" + System.lineSeparator());
        this.dos.writeBytes("Content-Length: " + lengthOfBodyContent + System.lineSeparator());
        processHeaders();
        this.dos.writeBytes(System.lineSeparator());
    }

    public void responseBody(byte[] body) throws IOException {
        this.dos.write(body, 0, body.length);
        this.dos.writeBytes(System.lineSeparator());
        this.dos.flush();
    }

    public void processHeaders() throws IOException {
        Set<String> keySet = this.header.keySet();
        for(String key: keySet) {
            this.dos.writeBytes(key + ": " + this.header.get(key) + System.lineSeparator());
        }
    }
}
