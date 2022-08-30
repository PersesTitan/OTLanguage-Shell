package origin.variable.controller;

import event.list.ListSetting;
import http.items.HttpRepository;
import origin.exception.MatchException;
import origin.exception.MatchMessage;
import origin.exception.VariableException;
import origin.exception.VariableMessage;
import origin.variable.define.VariableCheck;
import origin.variable.define.VariableKind;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//변수에 값을 넣는 작업
public class SetVariable {
    private final String patternText = "^\\s*[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_-]+:";
    private final Pattern pattern = Pattern.compile(patternText);

    public boolean check(String line) {
        Pattern pattern = Pattern.compile(patternText + "[^:]+");
        return pattern.matcher(line).find();
    }

    public void start(String line,
                      Map<String, Map<String, Object>> repository,
                      Set<String> set) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String originValue = matcher.group().strip(); //변수명:
            String key = originValue.substring(0, originValue.length()-1); //변수명: => 변수명
            String value = line.replaceFirst(patternText, ""); //변수에 들어갈 값
            //리스트인지 변수인지 확인하는 로직
            VariableKind variableKind = VariableCheck.checkVariableKind(VariableCheck.getCheck(key));
            if (!set.contains(key)) throw new VariableException(key + VariableMessage.doNotFind);
            if (variableKind.equals(VariableKind.LIST)) {
                // <<변수에 들어갈 값
                if (!value.startsWith("<<")) throw new MatchException(MatchMessage.grammarError);
                value = value.replaceFirst("^<<", ""); //변수에 들어갈 값
                for (String keys : repository.keySet()) {
                    if (repository.get(keys).containsKey(key)) {
                        List<Object> list = (List<Object>) repository.get(keys).get(key);
                        ListSetting.checkList(list, value, keys);
                        return;
                    }
                }
            } else {
                if (HttpRepository.partMap.containsKey(key)) HttpRepository.partMap.put(key, value);
                for (String keys : repository.keySet()) {
                    if (repository.get(keys).containsKey(key))
                        repository.get(keys).put(key, value.strip()); //변수명:값 => 값을 넣는 작업
                }
            }

        }
    }
}
