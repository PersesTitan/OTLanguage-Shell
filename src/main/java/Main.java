import origin.item.ActivityItem;
import origin.item.Setting;

import java.util.Scanner;

public class Main extends Setting implements ActivityItem {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        super.varClear();
        Scanner scanner = new Scanner(System.in);
        int stack = 0;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("OTLanguage 종료 됨")));

        while (true) {
            try {
                System.out.print(">>> ");
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("exit")) break;
                stack += count(line, '{');
                if (stack == 0) super.start(line);
                else if (stack > 0) {
                    total.append(line);
                    while (stack > 0) {
                        Scanner sc = new Scanner(System.in);
                        System.out.print("--- ");
                        String t = sc.nextLine();
                        stack += count(t, '{');
                        stack -= count(t, '}');
                        total.append(t).append("\n");
                    }
                    for (String l : bracket.bracket(total.toString().replace("{", "{\n")).split("\\n"))
                        super.start(l);
                    total.setLength(0);
                } else stack = 0;

            } catch (Exception e) {
                System.err.println("\n" + e.getMessage());
            }
        }
    }

    private int count(String line, char c) {
        int count = 0;
        for (int i = 0; i<line.length(); i++) {
            if (line.charAt(i) == c) count++;
        }
        return count;
    }
}
