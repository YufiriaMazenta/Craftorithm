package top.oasismc.oasisrecipe.util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map<String, String> newHashMap(String... str) {
        Map<String, String> hashMap = new HashMap<>();
        if (str.length % 2 != 0) {
            throw new IllegalArgumentException("Must be an even number of arguments");
        }
        for (int i = 0; i < str.length; i += 2) {
            hashMap.put(str[i], str[i + 1]);
        }
        return hashMap;
    }

}
