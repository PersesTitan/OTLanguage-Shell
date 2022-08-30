package http.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import event.Setting;
import http.define.HttpMethodType;
import http.handler.HandlerDao;
import http.handler.HttpGetHandler;
import http.handler.HttpPostHandler;
import http.items.HttpRepository;
import origin.exception.MatchException;
import origin.exception.MatchMessage;
import origin.exception.VariableException;
import origin.exception.VariableMessage;
import origin.variable.define.VariableCheck;
import origin.variable.model.Repository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class RootHandler extends Setting implements HttpHandler, HttpRepository {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try (exchange; OutputStream responsive = exchange.getResponseBody()) {
            if (HttpRepository.pathMap.containsKey(path)) {
                String filePath = new CreateHTML().changeVariable(HttpRepository.pathMap.get(path));
                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(filePath);

                int contentLen = byteBuffer.limit();
                byte[] content = new byte[contentLen];
                byteBuffer.get(content, 0, contentLen);

                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "text/html;charset=UTF-8");
                headers.add("Content-Length", String.valueOf(contentLen));

                HttpMethodType method = exchange.getRequestMethod().equals("POST") ?
                        HttpMethodType.POST : HttpMethodType.GET;
                String query = exchange.getRequestURI().getQuery();

                HandlerDao value = method.equals(HttpMethodType.POST) ?
                        new HttpPostHandler().handle(exchange) :
                        new HttpGetHandler().handle(exchange);

                //get, /, name=get
                printLog(method, path, value.value());

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, contentLen);
                responsive.write(content);

                startPath(value, method);
            } else {
                if (!path.equals("/favicon.ico")) System.out.println(path + "가 정의되어 있지 않습니다.");
            }
        }
    }

    private void startPath(HandlerDao handlerDao, HttpMethodType httpMethodType) {
        Map<String, String> method = HttpMethodType.POST.equals(httpMethodType) ? POST : GET;
        Map<String, Object> parameters = handlerDao.parameters();
        String path = handlerDao.path();
        if (method.containsKey(path)) {
            String queryAndId = method.get(path).strip(); //[변수1:query1, 변수2:query2] id
            String[] queryS = queryAndId.substring(1).split("]");
            if (queryS.length != 2) throw new MatchException(MatchMessage.grammarError);
            String[] qs = queryS[0].split(","); //[변수1:query1, 변수2:query2]
            String id = queryS[1].strip();

            for (String q : qs) {
                String[] variables = q.trim().split(":");
                if (variables.length != 2) throw new MatchException(MatchMessage.grammarError);
                String var = variables[0].trim();
                Object value = parameters.get(variables[1].trim());
                if (value == null) value = "";
                if (VariableCheck.check(var, VariableCheck.getCheck(value.toString())))
                    VariableCheck.setValue(var, value);
            }
            String uuids = Repository.uuidMap.getOrDefault(id, "");
            for (String line : uuids.split("\\n")) start(line);
        }
    }
}
