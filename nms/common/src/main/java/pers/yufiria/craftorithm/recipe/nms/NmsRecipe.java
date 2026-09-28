package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.NamespacedKey;
import org.bukkit.World;

public interface NmsRecipe<Input> {

    NamespacedKey recipeKey();

    /**
     * 判断输入物品内容是否匹配
     * <p>
     * {@code world} 是转换后的 Bukkit 世界
     */
    boolean matches(Input input, World world);

}
