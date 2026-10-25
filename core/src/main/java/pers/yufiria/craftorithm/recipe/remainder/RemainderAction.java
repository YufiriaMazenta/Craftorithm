package pers.yufiria.craftorithm.recipe.remainder;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 计算某个合成材料在一次合成后留在原槽位的物品
 * <p>
 * 自定义动作可通过 {@link RemainderActionRegistry#register} 注册
 */
public interface RemainderAction {

    /**
     * @param source 本次合成被消耗的原物品, 调用方保证数量为1
     * @return 留在槽位的物品, 返回空物品表示没有剩余物
     */
    @NotNull
    ItemStack createRemainder(@NotNull ItemStack source);

    /**
     * 动作工厂, 从单条配置创建动作
     */
    @FunctionalInterface
    interface Factory {

        /**
         * @return 配置无效时返回null, 并自行输出警告
         */
        @Nullable RemainderAction create(@NotNull ConfigurationSection section, @NotNull NamespacedKey recipeKey);

    }

}
