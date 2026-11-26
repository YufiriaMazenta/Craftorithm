package pers.yufiria.craftorithm.recipe.worldcondition;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.ArrayList;
import java.util.List;

/**
 * 把配方配置里的世界条件解析成 {@link WorldRule}
 * <p>
 * 只在配方注册时执行一次，解析结果不可变；配置有任何不合法之处都会抛出 {@link RecipeLoadException}。
 */
public final class WorldConditionParser {

    public static WorldRule parse(ConfigurationSection recipeConfig, NamespacedKey recipeKey) {
        if (recipeConfig.contains(WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY)) {
            throw new RecipeLoadException(
                "'" + WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY + "' has been removed, use '"
                    + WorldConditionHandler.WORLD_CONDITIONS_KEY + ".name' instead"
            );
        }

        ConfigurationSection worldConditions = recipeConfig.getConfigurationSection(
            WorldConditionHandler.WORLD_CONDITIONS_KEY
        );
        if (worldConditions == null) {
            //没写值等同于没配置, 但写了非配置节的内容说明格式用错了
            if (recipeConfig.get(WorldConditionHandler.WORLD_CONDITIONS_KEY) != null) {
                throw new RecipeLoadException(
                    "'" + WorldConditionHandler.WORLD_CONDITIONS_KEY + "' must be a configuration section"
                );
            }
            return WorldRule.AllowAll.INSTANCE;
        }

        List<WorldCondition> conditions = new ArrayList<>();
        for (String key : worldConditions.getKeys(false)) {
            WorldConditionType type = WorldConditionRegistry.INSTANCE.get(key);
            if (type == null) {
                throw new RecipeLoadException(
                    "Unknown world condition '" + key + "', available conditions: "
                        + WorldConditionRegistry.INSTANCE.keys()
                );
            }
            if (worldConditions.get(key) == null) {
                throw new RecipeLoadException(
                    WorldConditionHandler.WORLD_CONDITIONS_KEY + "." + key + " has no value"
                );
            }
            conditions.add(type.parse(worldConditions, key, recipeKey));
        }
        if (conditions.isEmpty()) {
            return WorldRule.AllowAll.INSTANCE;
        }
        return new WorldRule.Conditions(conditions);
    }

}
