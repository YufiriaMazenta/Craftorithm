package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.Nullable;

public interface RecipeRegister {

    /**
     * 注册配方
     *
     * @param recipe        要注册的配方
     * @param sync2Client 是否在变更后同步给客户端
     * @return
     */
    boolean registerRecipe(Recipe recipe, boolean sync2Client);

    /**
     * 注册配方，并传入配方配置以供指纹系统使用。
     * 默认实现忽略配置，直接调用 registerRecipe。
     *
     * @param recipe        要注册的配方
     * @param sync2Client 是否在变更后同步给客户端
     * @param recipeConfig  配方的配置文件
     */
    default boolean registerRecipe(Recipe recipe, boolean sync2Client, @Nullable ConfigurationSection recipeConfig) {
        return registerRecipe(recipe, sync2Client);
    }

    /**
     * 卸载/删除配方
     *
     * @param recipeKey     要删除的配方key
     * @param sync2Client 是否在变更后同步给客户端
     * @return
     */
    boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client);

}
