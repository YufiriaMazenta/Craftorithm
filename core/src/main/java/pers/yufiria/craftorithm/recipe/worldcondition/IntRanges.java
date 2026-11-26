package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 一组闭区间，命中任意一个即通过
 * <p>
 * 配置里写一个数字表示等于该数字，写 {@code min} / {@code max} 表示范围（只写一边即单边限制），
 * 也可以写成上述两者的列表。
 */
public record IntRanges(List<IntRange> ranges) {

    public IntRanges {
        ranges = List.copyOf(ranges);
    }

    public boolean contains(int value) {
        for (IntRange range : ranges) {
            if (range.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public static IntRanges parse(ConfigurationSection section, String key) {
        List<IntRange> ranges = new ArrayList<>();
        collect(section, key, section.get(key), ranges);
        if (ranges.isEmpty()) {
            throw new RecipeLoadException(ConditionValues.path(section, key) + " requires at least one value");
        }
        return new IntRanges(ranges);
    }

    private static void collect(ConfigurationSection section, String key, Object node, List<IntRange> ranges) {
        if (node instanceof Collection<?> values) {
            for (Object value : values) {
                collect(section, key, value, ranges);
            }
            return;
        }
        Map<?, ?> bounds = boundsOf(node);
        if (bounds != null) {
            ranges.add(parseRange(section, key, bounds));
            return;
        }
        if (node instanceof Integer value) {
            ranges.add(new IntRange(value, value));
            return;
        }
        throw ConditionValues.invalid(section, key, node);
    }

    /**
     * 取出 min/max 所在的键值表
     * <p>
     * 直接写在配置节下的映射会被解析成配置节，而列表里的映射只是原始的 Map
     */
    private static @Nullable Map<?, ?> boundsOf(Object node) {
        if (node instanceof ConfigurationSection rangeSection) {
            return rangeSection.getValues(false);
        }
        if (node instanceof Map<?, ?> map) {
            return map;
        }
        return null;
    }

    private static IntRange parseRange(ConfigurationSection section, String key, Map<?, ?> bounds) {
        for (Object field : bounds.keySet()) {
            if (!"min".equals(field) && !"max".equals(field)) {
                throw new RecipeLoadException(
                    ConditionValues.path(section, key) + " only supports 'min' and 'max', got '" + field + "'"
                );
            }
        }
        Integer min = readBound(section, key, bounds, "min");
        Integer max = readBound(section, key, bounds, "max");
        if (min == null && max == null) {
            throw new RecipeLoadException(
                ConditionValues.path(section, key) + " requires at least one of 'min' and 'max'"
            );
        }
        int lowerBound = min == null ? Integer.MIN_VALUE : min;
        int upperBound = max == null ? Integer.MAX_VALUE : max;
        if (lowerBound > upperBound) {
            throw new RecipeLoadException(
                ConditionValues.path(section, key) + " has min (" + lowerBound + ") greater than max (" + upperBound + ")"
            );
        }
        return new IntRange(lowerBound, upperBound);
    }

    private static Integer readBound(ConfigurationSection section, String key, Map<?, ?> bounds, String bound) {
        Object value = bounds.get(bound);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer intValue) {
            return intValue;
        }
        throw new RecipeLoadException(ConditionValues.path(section, key) + "." + bound + " must be an integer");
    }

}
