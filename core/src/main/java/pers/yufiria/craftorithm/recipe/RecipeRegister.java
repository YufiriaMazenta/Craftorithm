package pers.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public interface RecipeRegister {

    boolean registerRecipe(Recipe recipe);

    /**
     * 注册配方，并传入配方配置以供指纹系统使用。
     * 默认实现忽略配置，直接调用 registerRecipe。
     */
    default boolean registerRecipe(Recipe recipe, @Nullable ConfigurationSection recipeConfig) {
        return registerRecipe(recipe);
    }

    boolean unregisterRecipe(NamespacedKey recipeKey);

}
