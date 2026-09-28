package pers.yufiria.craftorithm.recipe.nms.common;

import org.bukkit.World;

/**
 * 用于将nms的输入内容转化为{@link BukkitCraftingInput}
 * <p>
 * 有序与无序配方的版本实现都只需要实现这一个转换方法，其余取值与匹配由各自接口的默认方法提供
 */
public interface NmsCraftingRecipe<Input> extends NmsRecipe {

    /**
     * 把 NMS 合成格逐格转换为 Bukkit 合成格（行优先展开）
     */
    BukkitCraftingInput toBukkitCraftingInput(Input input);

    boolean matches(Input input, World world);

}
