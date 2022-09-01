package origin.variable.controller;

import origin.exception.VariableException;
import origin.exception.VariableMessage;
import origin.variable.define.VariableCheck;
import origin.variable.define.VariableType;
import origin.variable.model.Repository;
import origin.variable.model.VariableWork;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MakeVariable implements VariableWork, Repository {
    private final String variable;  //ㅇㅅㅇ, ㅇㅁㅇ, ...
    private final VariableType varType;  //Integer, Long, ...
    private final Pattern pattern;
    private final String varPattern;

    public MakeVariable(String variable, VariableType varType) {
        this.variable = variable;
        this.varType = varType;
        this.varPattern = "^\\s*" + variable + "\\s+[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_-]+:";
        this.pattern = Pattern.compile(varPattern);
        repository.put(variable, new HashMap<>());
    }

    @Override
    public boolean check(String line) {
        return pattern.matcher(line).find();
    }

    @Override
    public void start(String line,
                      Map<String, Map<String, Object>> repository,
                      Set<String> set) {
        String patternText = "^\\s*" + variable + "\\s+";
        Pattern pattern = Pattern.compile(patternText); // ㅇㅅㅇ, ㅇㅁㅇ, ...
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String type = matcher.group().strip();  //ㅇㅅㅇ, ㅇㅁㅇ, ...
            if (!repository.containsKey(type)) throw new VariableException(type + VariableMessage.doNotDefine);
            //변수:초기값 => 변수 초기값
            StringTokenizer tokenizer = new StringTokenizer(line.replaceAll(patternText, ""), ":");
            String key = tokenizer.nextToken(); //변수
            String value = line.replaceFirst(varPattern, ""); //초기값
            if (set.contains(key)) throw new VariableException(key + VariableMessage.sameVariable);
            if (!VariableCheck.check(value, varType)) throw new VariableException(VariableMessage.typeMatchError);
            //저장소에 값 저장
            repository.get(type).put(key, value);
            set.add(key);
        }
    }
}
