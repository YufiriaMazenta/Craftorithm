package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

/**
 * 世界条件的配置解析器
 * <p>
 * 注册到 {@link WorldConditionRegistry} 后，配方即可通过同名配置键使用该条件。
 * 值不合法时抛出 {@link RecipeLoadException}，由 {@link pers.yufiria.craftorithm.recipe.worldcondition.WorldConditionParser}
 * 统一记录日志并将配方置为不可用。
 */
@FunctionalInterface
public interface WorldConditionType {

    /**
     * 读取并校验配置，构造条件
     *
     * @param section   世界条件所在的配置节
     * @param key       条件对应的配置键
     * @param recipeKey 当前配方，仅用于日志定位
     */
    WorldCondition parse(ConfigurationSection section, String key, NamespacedKey recipeKey);

}
