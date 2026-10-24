package pers.yufiria.craftorithm.recipe.remainder;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 计算某个合成材料在一次合成后留在原槽位的物品
 * <p>
 * 自定义类型可通过 {@link RemainderProcessorRegistry#register} 注册
 */
public interface RemainderProcessor {

    String TYPE_DAMAGE = "damage";
    String TYPE_REPLACE = "replace";
    String TYPE_KEEP = "keep";

    /**
     * @param source 本次合成被消耗的原物品, 调用方保证数量为1
     * @return 留在槽位的物品, 返回空物品表示没有剩余物
     */
    @NotNull
    ItemStack createRemainder(@NotNull ItemStack source);

}
