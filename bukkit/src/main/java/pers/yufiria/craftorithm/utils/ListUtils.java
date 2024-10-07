package pers.yufiria.craftorithm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListUtils {

    public static <T> boolean isAllNull(List<T> list) {
        return list.stream().allMatch(Objects::isNull);
    }

    public static <T> List<List<T>> removeEmptyColumnAndLine(List<List<T>> matrix) {
        // 删除开头的空行
        while (!matrix.isEmpty() && matrix.get(0).stream().allMatch(Objects::isNull)) {
            matrix.remove(0);
        }
        // 删除末尾的空行
        while (!matrix.isEmpty() && matrix.get(matrix.size() - 1).stream().allMatch(Objects::isNull)) {
            matrix.remove(matrix.size() - 1);
        }

        // 找到第一列和最后一列的索引
        final int[] startCol = {0};
        final int[] endCol = {matrix.isEmpty() ? -1 : matrix.get(0).size() - 1};

        // 删除开头的空列
        while (startCol[0] <= endCol[0] && matrix.stream().allMatch(row -> row.get(startCol[0]) == null)) {
            startCol[0] ++;
        }
        // 删除末尾的空列
        while (endCol[0] >= startCol[0] && matrix.stream().allMatch(row -> row.get(endCol[0]) == null)) {
            endCol[0] --;
        }

        // 生成新的二维列表
        List<List<T>> result = new ArrayList<>();
        for (List<T> row : matrix) {
            List<T> newRow = new ArrayList<>(row.subList(startCol[0], endCol[0] + 1));
            result.add(newRow);
        }

        return result;
    }

}
