package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.inventory.Recipe;

import java.util.function.Supplier;

/**
 * Bukkit 配方的懒加载缓存
 * <p>
 * 各版本的 NMS 配方都用它保存还原后的 Bukkit 配方，避免每次调用都重新构建
 */
public final class CachedBukkitRecipe {

    private volatile Recipe cached;

    /**
     * 返回已缓存的 Bukkit 配方，尚未构建时通过 {@code bukkitRecipeCreator} 构建并缓存
     */
    public Recipe get(Supplier<Recipe> bukkitRecipeCreator) {
        Recipe cachedRecipe = cached;
        if (cachedRecipe != null) {
            return cachedRecipe;
        }
        Recipe createdRecipe = bukkitRecipeCreator.get();

        cached = createdRecipe;
        return createdRecipe;
    }

}