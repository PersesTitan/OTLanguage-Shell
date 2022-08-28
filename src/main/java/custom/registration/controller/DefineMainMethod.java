package custom.registration.controller;

import custom.registration.define.RepositoryValue;
import custom.use.controller.UseCustomString;
import custom.use.controller.UseCustomVoid;
import custom.use.define.BracketSplit;
import origin.exception.VariableException;
import origin.exception.VariableMessage;
import origin.variable.model.Repository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefineMainMethod implements BracketSplit {
    private final String returnText = "=>";
    //uuid=>~~메소드명
    private final String returnPatternText = "=>\\s*~*[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_-]+\\s*$";
    private final Pattern returnPattern = Pattern.compile(returnPatternText);
    private final String patternText;
    private final Pattern pattern;
    private final String uuidText; //ㅋㅅㅋ 클래스명[ㅇㅁㅇ 변수명]
    private final String text; //ㅁㅅㅁ

    public DefineMainMethod(String patternText) {
        //builder : [ㅇㅁㅇ 변수명]
        StringBuilder builder = new StringBuilder("(\\[\\s*(");
        Repository.repository.keySet().forEach(key -> builder.append(key).append("|"));
        builder.deleteCharAt(builder.length()-1);
        builder.append(")\\s+[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_-]+\\s*])+");
        this.patternText = builder.toString(); //[ㅇㅁㅇ 변수명]
        // ㅁㅅㅁ 변수명[ㅇㅁㅇ 변수명]
        this.uuidText = "^\\s*" + patternText + "\\s+[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9_-]+" + this.patternText;
        this.pattern = Pattern.compile(this.uuidText);
        this.text = patternText; // ㅁㅅㅁ
    }

    public boolean check(String line) {
        return pattern.matcher(line).find();
    }

    public void start(String line) {
        Matcher matcher = this.pattern.matcher(line); // ㅁㅅㅁ 메소드명[ㅇㅁㅇ 변수명]
        if (matcher.find()) {
            String group = matcher.group().strip(); //ㅁㅅㅁ 메소드명[ㅇㅁㅇ 변수명]
            RepositoryValue variables = getRepositoryValue(group, patternText); //변수타입, 변수명
            String methodName = group //메소드명 추출
                    .replaceFirst(patternText, "")
                    .replaceFirst("^\\s*"+this.text+"\\s*", "")
                    .strip();

            if (Repository.methods.contains(methodName)) //중복 검사
                throw new VariableException(methodName + VariableMessage.sameMethod);
            Repository.methods.add(methodName);
            // uuid 추출하기
            String uuid = line.replaceFirst(uuidText, "").strip(); // uuid => 변수명
            returnValue(uuid, methodName, variables);
        }
    }

    private void returnValue(String line, String methodName, RepositoryValue repositoryValue) {
        var mainRepository = Repository.repository;
        var mainSet = Repository.set;
        //uuid=>~~메소드명
        Matcher matcher = returnPattern.matcher(line);
        if (matcher.find()) {
            //=>~~메소드명 -> ~~메소드명
            String group = matcher.group().replaceFirst(returnText, "").strip();
            String uuid = line.replaceFirst(returnPatternText, "").strip(); //uuid
            if (Repository.uuidMap.containsKey(uuid)) {
                String total = Repository.uuidMap.get(uuid);
                Repository.useCustomStrings.add(
                        new UseCustomString(methodName, total, repositoryValue, mainRepository, mainSet, group));
            }
        } else {
            String uuid = line.strip();
            if (Repository.uuidMap.containsKey(uuid)) {
                String total = Repository.uuidMap.get(uuid);
                Repository.useCustomVoids.add(
                        new UseCustomVoid(methodName, total, repositoryValue, mainRepository, mainSet));
            }
        }
    }
}
