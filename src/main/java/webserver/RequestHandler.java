package webserver;

import com.google.common.io.Files;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello World".getBytes();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String requestFirst = bufferedReader.readLine();
            log.info("request first {}", requestFirst);
            String[] requestArray = requestFirst.split(" ");
            String x = null;
            Map<String, String> cookies = null;
            int contentLength = 0;
            String accept = "text/html;";
            while ((x = bufferedReader.readLine()) != null) {
                if (x.contains("Cookie")) {
                    cookies = HttpRequestUtils.parseCookies(x.split(":")[1].trim());
                }
                if (x.contains("Content-Length")) {
                    contentLength = Integer.parseInt(x.split(":")[1].trim());
                }
                if (x.contains("Accept:")) {
                    accept = x.split(":")[1].split(";")[0].trim();
                }
                log.info("{}", x);

                if (x.equals("")) break;
            }

            if (requestArray[0].equals("GET")) {
                if (requestArray[1].startsWith("/index.html")) {
                    body = Files.toByteArray(chooseSendFile("index.html"));
                    response200Header(dos, body.length);
                } else if (requestArray[1].startsWith("/user/login.html")) {
                    body = Files.toByteArray(chooseSendFile("user/login.html"));
                    response200Header(dos, body.length);
                } else if (requestArray[1].startsWith("/user/form.html")) {
                    body = Files.toByteArray(chooseSendFile("user/form.html"));
                    response200Header(dos, body.length);
                } else if (requestArray[1].startsWith("/user/create")) {
                    Map<String, String> map = HttpRequestUtils.parseQueryString(requestFirst.split(" ")[1].split("\\?")[1].trim());
                    DataBase.addUser(new User(
                            map.get("userId"),
                            URLDecoder.decode(map.get("password")),
                            URLDecoder.decode(map.get("name")),
                            URLDecoder.decode(map.get("email"))
                    ));

                    body = Files.toByteArray(chooseSendFile("index.html"));
                    response302Header(dos);
                } else if (requestArray[1].startsWith("/user/list")) {
                    StringBuilder returnSb = new StringBuilder();
                    if (Boolean.parseBoolean(cookies.get("login"))) {
                        StringBuilder sb = new StringBuilder();
                        ArrayList<User> users = new ArrayList<>(DataBase.findAll());
                        for (int i=0; i<users.size(); i++) {
                            User user = users.get(i);
                            sb.append("<tr>")
                                    .append("<th scope=\"row\">" + (i+1) + "</th>")
                                    .append("<td>").append(user.getUserId()).append("</td>")
                                    .append("<td>").append(user.getName()).append("</td>")
                                    .append("<td>").append(user.getEmail()).append("</td>")
                            .append("</tr>");
                        }

                        BufferedReader fileReader = new BufferedReader(new FileReader(chooseSendFile("user/list_a.html")));
                        while ((x = fileReader.readLine()) != null) {
                            if (x.contains("{tbody_is_here}")) {
                                returnSb.append(x.replace("{tbody_is_here}", sb.toString()));
                                continue;
                            }
                            returnSb.append(x);
                        }

                        body = returnSb.toString().getBytes(StandardCharsets.UTF_8);
                        response200Header(dos, body.length);
                    } else {
                        body = Files.toByteArray(chooseSendFile("user/login.html"));
                        response200Header(dos, body.length);
                    }
                } else if (requestArray[1].endsWith(".css")) {
                    body = Files.toByteArray(chooseSendFile(requestArray[1]));
                    response200HeaderCss(dos, body.length);
                    responseBody(dos, body);
                } else if (requestArray[1].endsWith(".js")) {
                    body = Files.toByteArray(chooseSendFile(requestArray[1]));
                    response200HeaderJs(dos, body.length);
                    responseBody(dos, body);
                } else {
                    body = "Hello World".getBytes();
                    response200Header(dos, body.length);
                }
            }

            if (requestArray[0].equals("POST")) {
                Map<String, String> map = HttpRequestUtils.parseQueryString(IOUtils.readData(bufferedReader, contentLength));

                if (requestArray[1].startsWith("/user/create")) {
                    DataBase.addUser(new User(
                            map.get("userId"),
                            URLDecoder.decode(map.get("password")),
                            URLDecoder.decode(map.get("name")),
                            URLDecoder.decode(map.get("email"))
                    ));
                    body = Files.toByteArray(chooseSendFile("index.html"));
                    response302Header(dos);
                } else if (requestArray[1].startsWith("/user/login")) {
                    User findUser = DataBase.findUserById(map.get("userId"));
                    boolean correct = findUser.getUserId().equals(map.get("userId"))
                            && URLDecoder.decode(findUser.getPassword()).equals(map.get("password"));
                    body = Files.toByteArray(chooseSendFile("user/login_failed.html"));
                    if (correct) body = Files.toByteArray(chooseSendFile("index.html"));
                    response200HeaderLogin(dos, body.length, correct);
                }
            }

            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        log.info("response html");
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderCss(DataOutputStream dos, int lengthOfBodyContent) {
        log.info("response css");
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderJs(DataOutputStream dos, int lengthOfBodyContent) {
        log.info("response js");
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: */* \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderLogin(DataOutputStream dos, int lengthOfBodyContent, boolean correct) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Set-cookie: login=" + correct + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private File chooseSendFile(String url) {
        return new File("webapp/" + url);
    }
}
