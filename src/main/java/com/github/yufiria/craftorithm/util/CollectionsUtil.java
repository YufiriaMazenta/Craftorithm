package com.github.yufiria.craftorithm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class CollectionsUtil {

    public static Map<String, String> newStringHashMap(String... str) {
        Map<String, String> hashMap = new HashMap<>();
        if (str.length % 2 != 0) {
            throw new IllegalArgumentException("Must be an even number of arguments");
        }
        for (int i = 0; i < str.length; i += 2) {
            hashMap.put(str[i], str[i + 1]);
        }
        return hashMap;
    }

    public static String list2ArcencielBlock(List<String> list) {
        StringJoiner blockJoiner = new StringJoiner(" ");
        for (String s : list) {
            blockJoiner.add(s);
        }
        return blockJoiner.toString();
    }

}
