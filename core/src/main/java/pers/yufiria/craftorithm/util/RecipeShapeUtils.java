package pers.yufiria.craftorithm.util;

import java.util.List;

public enum RecipeShapeUtils {

    ;

    /**
     * 移除配方形状中首尾的全空白行（保留中间的空行）
     */
    public static void removeEmptyRow(List<String> shape) {
        while (!shape.isEmpty() && shape.getFirst().trim().isEmpty()) {
            shape.removeFirst();
        }
        while (!shape.isEmpty() && shape.getLast().trim().isEmpty()) {
            shape.removeLast();
        }
    }

    /**
     * 移除配方形状中首尾的全空白列（保留中间的空列）
     */
    public static void removeEmptyColumn(List<String> shape) {
        boolean[] empty = new boolean[3];
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            empty[i] = shape.stream().allMatch(s -> finalI >= s.length() || s.charAt(finalI) == ' ');
        }
        if (empty[0]) {
            if (empty[1]) {
                if (!empty[2]) {
                    shape.replaceAll(s -> s.length() > 2 ? s.substring(2) : "");
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.length() >= 2 ? s.substring(1, 2) : "");
                } else {
                    shape.replaceAll(s -> s.length() >= 2 ? s.substring(1) : "");
                }
            }
        } else {
            if (empty[1]) {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, 1));
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, Math.min(2, s.length())));
                }
            }
        }
    }

}
