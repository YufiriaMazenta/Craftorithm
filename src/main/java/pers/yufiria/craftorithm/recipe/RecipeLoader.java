package pers.yufiria.craftorithm.recipe;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @param <R> 配方的类型
 */
public interface RecipeLoader<R extends Recipe> {

    /**
     * 加载配方,并将配方注册
     * @param recipeConfig 配方配方的加载源
     */
    @Nullable R loadRecipe(String recipeKey, ConfigurationSection recipeConfig);

}
