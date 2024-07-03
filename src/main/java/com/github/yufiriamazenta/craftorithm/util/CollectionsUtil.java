package com.github.yufiriamazenta.craftorithm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    /**
     * 将一个map中所有符合条件的键值对插入另外一个map
     * @param map 被插入的map
     * @param otherMap 插入的map
     * @param condition 筛选条件，两个参数都对应用于插入的map中的值
     * @param <K> 用于插入的map的key
     * @param <V> 用于插入的map的value
     */
    public static <K, V> void putAllIf(Map<K, V> map, Map<K, V> otherMap, BiFunction<K, V, Boolean> condition) {
        otherMap.forEach((k, v) -> {
            if (condition.apply(k, v)) {
                map.put(k, v);
            }
        });
    }

}
