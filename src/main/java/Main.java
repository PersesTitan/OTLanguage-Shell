import item.ActivityItem;
import item.Setting;

import java.util.Scanner;

public class Main extends Setting implements ActivityItem {

    public static void main(String[] args) {
        new Main(args);
    }

    private Main(String[] args) {
        super.varClear();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(">>> ");
                super.start(scanner.nextLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
