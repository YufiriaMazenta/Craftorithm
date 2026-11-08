package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

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
     * 卸载/删除配方
     *
     * @param recipeKey     要删除的配方key
     * @param sync2Client 是否在变更后同步给客户端
     * @return
     */
    boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client);

}
