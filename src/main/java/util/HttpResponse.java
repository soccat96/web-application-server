package util;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final HashMap<String, String> header = new HashMap<>();
    private DataOutputStream dos;
    private byte[] body;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void addHeader(String key, String value) {
        header.put(key, value);
    }

    public void forward(String url) throws IOException {
        forwardBody(url);

        response200Header();
        if(url.endsWith(".css")) {
            addHeader("Content-Type", "text/css");
        } else if(url.endsWith(".js")) {
            addHeader("Content-Type", "*/*");
        } else {
            addHeader("Content-Type", "text/html;charset=utf-8");
        }
        addHeader("Content-Length", String.valueOf(this.body.length));
        processHeader();

        responseBody(this.body);
    }

    public void sendRedirect(String url) throws IOException {
        this.dos.writeBytes("HTTP/1.1 302 Found" + System.lineSeparator());
        this.dos.writeBytes("Location: " + url + System.lineSeparator());
        processHeader();
        this.dos.writeBytes(System.lineSeparator());
    }

    public void forwardBody(String url) throws IOException {
        this.body = Files.toByteArray(new File("webapp" + url));
    }

    public void response200Header() throws IOException {
        this.dos.writeBytes("HTTP/1.1 200 OK" + System.lineSeparator());
    }

    public void responseBody(byte[] body) throws IOException {
        this.dos.write(body, 0, body.length);
        this.dos.flush();
    }

    public void processHeader() throws IOException {
        Set<String> keySet = this.header.keySet();
        for(String key: keySet) {
            this.dos.writeBytes(key + ": " + this.header.get(key) + System.lineSeparator());
        }
        this.dos.writeBytes(System.lineSeparator());
    }
}
