package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.choice.RecipeChoiceParser;

/**
 *
 * @param <R> 配方的类型
 */
public interface RecipeParser<R extends Recipe> {

    @NotNull RecipeChoiceParser choiceParser();

    /**
     * 从配置文件里解析配方
     * @param recipeConfig 配方的加载源
     */
    @Nullable R parse(String recipeName, ConfigurationSection recipeConfig);

}
