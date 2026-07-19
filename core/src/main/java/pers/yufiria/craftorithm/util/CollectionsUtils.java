package pers.yufiria.craftorithm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class CollectionsUtils {

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

    /**
     * 移除二维列表首尾的全空白行（保留中间的空行）
     * @param grid 二维列表
     * @param isEmpty 判断元素是否为空的谓词
     */
    public static <T> void trimEmptyRows(List<List<T>> grid, Predicate<T> isEmpty) {
        while (!grid.isEmpty() && grid.getFirst().stream().allMatch(isEmpty)) {
            grid.removeFirst();
        }
        while (!grid.isEmpty() && grid.getLast().stream().allMatch(isEmpty)) {
            grid.removeLast();
        }
    }

    /**
     * 移除二维列表首尾的全空白列（保留中间的空列）
     * @param grid 二维列表
     * @param isEmpty 判断元素是否为空的谓词
     */
    public static <T> void trimEmptyColumns(List<List<T>> grid, Predicate<T> isEmpty) {
        if (grid.isEmpty()) {
            return;
        }
        int maxCol = grid.stream().mapToInt(List::size).max().orElse(0);
        int firstNonEmpty = -1;
        for (int col = 0; col < maxCol; col++) {
            int finalCol = col;
            boolean allEmpty = grid.stream().allMatch(row -> finalCol >= row.size() || isEmpty.test(row.get(finalCol)));
            if (!allEmpty) {
                firstNonEmpty = col;
                break;
            }
        }
        if (firstNonEmpty == -1) {
            grid.clear();
            return;
        }
        int lastNonEmpty = -1;
        for (int col = maxCol - 1; col >= 0; col--) {
            int finalCol = col;
            boolean allEmpty = grid.stream().allMatch(row -> finalCol >= row.size() || isEmpty.test(row.get(finalCol)));
            if (!allEmpty) {
                lastNonEmpty = col;
                break;
            }
        }
        for (List<T> row : grid) {
            if (row.size() > lastNonEmpty + 1) {
                row.subList(lastNonEmpty + 1, row.size()).clear();
            }
            if (firstNonEmpty > 0) {
                row.subList(0, firstNonEmpty).clear();
            }
        }
    }

    /**
     * 移除二维列表首尾的全空白行和列（保留中间的空白行/列）
     * @param grid 二维列表
     * @param isEmpty 判断元素是否为空的谓词
     */
    public static <T> void trimEmptyBorders(List<List<T>> grid, Predicate<T> isEmpty) {
        trimEmptyRows(grid, isEmpty);
        trimEmptyColumns(grid, isEmpty);
    }

}
