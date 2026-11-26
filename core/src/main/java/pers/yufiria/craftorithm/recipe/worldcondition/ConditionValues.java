package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 世界条件读取配置值的公共处理
 * <p>
 * 统一定义「键路径」的拼接方式，保证所有报错都能定位到具体的配置位置。
 */
final class ConditionValues {

    /**
     * 读取一个既可以是单值也可以是列表的文本配置
     */
    static List<String> readStringList(ConfigurationSection section, String key) {
        Object node = section.get(key);
        if (node instanceof String value) {
            return List.of(requireText(section, key, value));
        }
        if (node instanceof Collection<?> values) {
            List<String> result = new ArrayList<>(values.size());
            for (Object value : values) {
                if (!(value instanceof String text)) {
                    throw invalid(section, key, value);
                }
                result.add(requireText(section, key, text));
            }
            if (result.isEmpty()) {
                throw new RecipeLoadException(path(section, key) + " requires at least one value");
            }
            return result;
        }
        throw invalid(section, key, node);
    }

    /**
     * 拼接配置路径，用于报错时指明位置
     */
    static String path(ConfigurationSection section, String key) {
        String currentPath = section.getCurrentPath();
        if (currentPath == null || currentPath.isEmpty()) {
            return key;
        }
        return currentPath + "." + key;
    }

    static RecipeLoadException invalid(ConfigurationSection section, String key, Object value) {
        return new RecipeLoadException(path(section, key) + " has an invalid value '" + value + "'");
    }

    static RecipeLoadException invalidEnum(ConfigurationSection section, String key, String value, String allowedValues) {
        return new RecipeLoadException(
            path(section, key) + " has an invalid value '" + value + "', allowed values: " + allowedValues
        );
    }

    private static String requireText(ConfigurationSection section, String key, String value) {
        if (value.isBlank()) {
            throw new RecipeLoadException(path(section, key) + " cannot be empty");
        }
        return value;
    }

}
