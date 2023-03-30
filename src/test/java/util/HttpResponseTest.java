package util;

import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;

public class HttpResponseTest {
    private final String FILE_PATH = "src/test/java/util";

    @After
    public void deleteFiles() {
        File[] files = new File(FILE_PATH).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toString().endsWith(".txt");
            }
        });

        for(int i=0; i<files.length; i++) {
            files[i].delete();
        }
    }

    @Test
    public void responseForward() throws IOException {
        HttpResponse response = new HttpResponse(new FileOutputStream(FILE_PATH + "/responseForward.txt"));
        response.forward("/index.html");
    }

    @Test
    public void responseRedirect() throws IOException {
        HttpResponse response = new HttpResponse(new FileOutputStream(FILE_PATH + "/responseRedirect.txt"));
        response.sendRedirect("/index.html");
    }

    @Test
    public void responseCookies() throws IOException {
        HttpResponse response = new HttpResponse(new FileOutputStream(FILE_PATH + "/responseCookie.txt"));
        response.addHeader("Set-Cookie", "login=true");
        response.sendRedirect("/index.html");
    }
}