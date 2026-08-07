package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public interface RecipeRegister {

    /**
     * 注册配方
     * @param recipe 要注册的配方
     * @param updateRecipes 是否给玩家更新配方列表（这个字段实际上基本上只在启用CraftorithmRecipeRegister的时候才有用）
     * @return
     */
    boolean registerRecipe(Recipe recipe, boolean updateRecipes);

    /**
     * 注册配方，并传入配方配置以供指纹系统使用。
     * 默认实现忽略配置，直接调用 registerRecipe。
     * @param recipe 要注册的配方
     * @param updateRecipes 是否给玩家更新配方列表（这个字段实际上基本上只在启用CraftorithmRecipeRegister的时候才有用）
     * @param recipeConfig 配方的配置文件
     */
    default boolean registerRecipe(Recipe recipe, boolean updateRecipes, @Nullable ConfigurationSection recipeConfig) {
        return registerRecipe(recipe, updateRecipes);
    }

    /**
     * 卸载/删除配方
     * @param recipeKey 要删除的配方key
     * @param updateRecipes 是否给玩家更新配方列表（这个字段实际上基本上只在启用CraftorithmRecipeRegister的时候才有用）
     * @return
     */
    boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes);

}
