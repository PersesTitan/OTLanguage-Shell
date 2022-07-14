import event.Setting;
import http.server.Server;
import origin.exception.FileFailException;
import origin.exception.FileFailMessage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class Main extends Setting {

    public static void main(String[] args) {
        new Main(args);
    }

    private Main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("OTLanguage 종료 됨\n")));
        firstStart();

        if(args.length > 0) path = args[0];
        if (args.length == 2) fileRead(path + "/" + args[1]);
        else {
            Scanner scanner = new Scanner(System.in);
            int stack = 0;
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
    }

    private int count(String line, char c) {
        int count = 0;
        for (int i = 0; i<line.length(); i++) {
            if (line.charAt(i) == c) count++;
        }
        return count;
    }

    private void fileRead(String filePath) {
        if (!new File(filePath).canRead()) throw new FileFailException(FileFailMessage.doNotReadFile);
        if (!filePath.toLowerCase(Locale.ROOT).endsWith(".otl")) throw new FileFailException(FileFailMessage.notMatchExtension);
        String text;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            while ((text = reader.readLine()) != null) Setting.total.append(text).append("\n");
            //괄호 -> 고유 아이디로 전환 //괄호 계산
            String total = bracket.bracket(Setting.total.toString());
            for (String line : total.split("\\n")) {
                start(line); // 실행 메소드
            }
        } catch (IOException ignored) {}
        pause();
    }

    private void pause() {
        try {
            System.in.read();
        } catch (IOException ignored) {} finally {
            if (Server.httpServerManager != null) Server.httpServerManager.stop();
        }
        System.exit(0);
    }
}
