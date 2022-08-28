package origin.consol.controller;

import origin.consol.define.PrintWork;
import origin.variable.model.Repository;

import java.util.regex.Pattern;

public class Println implements PrintWork {
    private final String patternText;
    private final Pattern pattern;

    public Println(String patternText) {
        this.patternText = "(\\n|^)\\s*"+patternText+"($|\\s)";
        this.pattern = Pattern.compile(this.patternText);
    }

    @Override
    public boolean check(String line) {
        return pattern.matcher(line).find();
    }

    @Override
    public void start(String line) {
        System.out.println(line.replaceFirst(patternText, ""));
    }
}
