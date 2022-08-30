package system.start;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OSSetting {
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "a");
        System.out.println(map);
        System.out.println(Arrays.toString("클래스명~".split("~")));
    }

    // 슬래쉬 반환
    public static String sep() {
        return System.getProperty("file.separator");
    }
}
