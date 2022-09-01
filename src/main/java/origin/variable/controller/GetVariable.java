package origin.variable.controller;

import http.items.HttpRepository;
import origin.exception.MatchException;
import origin.exception.MatchMessage;
import origin.exception.VariableException;
import origin.exception.VariableMessage;
import origin.variable.model.Repository;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//변수를 불러와서 대치하는 작업
public class GetVariable {
    public static final String patternText = ":[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_-]+[ _]"; //:변수명( )
    private final Pattern pattern = Pattern.compile(patternText);

    public boolean check(String line) {
        Pattern pattern = Pattern.compile("[^:]?"+patternText);
        return pattern.matcher(line).find();
    }

    public String start(String line, Map<String, Map<String, Object>> repository) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String group = matcher.group();
            int pos = group.length();
            String key = group.substring(1, pos-1); //:변수명 => 변수명
            for (String keys : repository.keySet()) {
                if (repository.get(keys).containsKey(key))
                    line = line.replaceAll(patternText, repository.get(keys).get(key).toString());
                if (HttpRepository.partMap.containsKey(key))
                    line = line.replaceAll(patternText, HttpRepository.partMap.get(key));
            }
        }
        return line;
    }
}
