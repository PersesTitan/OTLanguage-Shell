package http.controller;

import http.items.HttpRepository;
import origin.exception.MatchException;
import origin.exception.MatchMessage;
import origin.loop.model.LoopWork;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//GET 과 POST 값 가져오는 작업
public class HttpGetPost implements HttpRepository, LoopWork {
    //[경로] [POST,GET]>ㅇㅅㅇ>[name]
    //[POST,GET][경로]>ㅇㅅㅇ>[변수1:query1, 변수2:query2]

//    private final String patternText = "\\S+\\s+(POST|GET)\\s*>ㅇㅅㅇ>\\s*\\S+";
    private final String patternText = "^\\s*(POST|GET)\\s*\\S+\\s*>ㅇㅅㅇ>\\s*\\[([^\\[\\]:,]+:[^\\[\\]:,]+)+]";
    private final Pattern pattern = Pattern.compile(patternText);

    @Override
    public boolean check(String line) {
        return pattern.matcher(line).find();
    }

    @Override
    public void start(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String[] group = matcher.group().strip().split(">ㅇㅅㅇ>");
            if (group.length != 2) throw new MatchException(MatchMessage.grammarError);
            String path = group[0].trim(); // POST/sub
            String variable = group[1].trim() + line.replaceFirst(patternText, ""); //[변수1:query1, 변수2:query2] id
            if (path.startsWith("POST")) {
                path = path.replaceFirst("^POST", "").trim();
                POST.put(path, variable);
            } else if (path.startsWith("GET")) {
                path = path.replaceFirst("^GET", "").trim();
                GET.put(path, variable);
            }
        }
    }

    @Override
    public String getPattern() {
        return patternText;
    }
}
