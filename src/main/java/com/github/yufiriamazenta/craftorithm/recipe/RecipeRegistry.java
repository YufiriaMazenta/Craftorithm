package com.github.yufiriamazenta.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

public interface RecipeRegistry {

    RegisterResult registerRecipe(Recipe recipe);

    /**
     * 重置已经注册的配方
     */
    void resetRecipes();

    UnregisterResult unregisterRecipe(NamespacedKey recipeKey);

    enum RegisterResult {
        SUCCESS, //成功注册
        DUPLICATE, //重复的配方
        UNSUPPORTED //不支持注册此种配方
    }

    enum UnregisterResult {
        SUCCESS, //成功取消注册
        NOT_EXIST, //不存在此配方
        UNSUPPORTED //不支持取消注册此配方
    }

}
