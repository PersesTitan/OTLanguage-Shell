package origin.consol.controller;

import origin.consol.define.PriorityPrintWork;
import origin.variable.model.Repository;

import java.util.regex.Pattern;

public class PriorityPrint implements PriorityPrintWork {
    private final String PATTERN;
    private final Pattern pattern;

    public PriorityPrint(String patternText) {
        this.PATTERN = "^\\s*"+patternText+"(\\s|$)";
        this.pattern = Pattern.compile(this.PATTERN);
    }

    @Override
    public void start(String line) {
        System.out.print(line.replaceFirst(PATTERN, ""));
    }

    @Override
    public boolean check(String line) {
        return pattern.matcher(line).find();
    }
}
