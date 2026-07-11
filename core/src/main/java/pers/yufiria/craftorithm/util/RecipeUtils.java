package pers.yufiria.craftorithm.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RecipeUtils {

    private static final Map<NamespacedKey, Tag<Material>> ITEMS_TAGS = new HashMap<>();
    private static final Map<NamespacedKey, Tag<Material>> BLOCKS_TAGS = new HashMap<>();

    static {
        Iterable<Tag<Material>> blocksTags = Bukkit.getTags("blocks", Material.class);
        for (Tag<Material> blocksTag : blocksTags) {
            BLOCKS_TAGS.put(blocksTag.getKey(), blocksTag);
        }

        Iterable<Tag<Material>> itemsTags = Bukkit.getTags("items", Material.class);
        for (Tag<Material> itemsTag : itemsTags) {
            ITEMS_TAGS.put(itemsTag.getKey(), itemsTag);
        }
    }

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

    public static Optional<Tag<Material>> getTag(String tagKeyStr) {
        if (tagKeyStr.contains(":")) {
            NamespacedKey tagKey = NamespacedKey.fromString(tagKeyStr);
            return Optional.ofNullable(getTag(tagKey));
        }
        //兼容旧版写法, 也就是直接在Tag里反射
        String upperTagKey = tagKeyStr.toUpperCase();
        Tag<Material> reflectTag;
        try {
            Field field = Tag.class.getField(upperTagKey);
            reflectTag = (Tag<Material>) field.get(null);
            return Optional.ofNullable(reflectTag);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        NamespacedKey tagKey = NamespacedKey.fromString(tagKeyStr);
        return Optional.ofNullable(getTag(tagKey));
    }

    private static @Nullable Tag<Material> getTag(NamespacedKey tagKey) {
        if (ITEMS_TAGS.containsKey(tagKey)) {
            return ITEMS_TAGS.get(tagKey);
        }
        if (BLOCKS_TAGS.containsKey(tagKey)) {
            return BLOCKS_TAGS.get(tagKey);
        }
        return null;
    }

}
