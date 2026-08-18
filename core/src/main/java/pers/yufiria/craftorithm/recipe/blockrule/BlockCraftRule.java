package pers.yufiria.craftorithm.recipe.blockrule;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 合成限制规则接口
 * <p>
 * 第三方插件可通过 {@link BlockCraftRuleRegistry#register(String, BlockCraftRuleFactory)} 注册自定义规则类型
 */
public interface BlockCraftRule {

    /**
     * 规则类型标识，对应配置中的 type 字段
     */
    @NotNull String type();

    /**
     * 判断物品是否应被阻止参与指定配方
     *
     * @param item      待检测物品
     * @param recipeKey 配方的 NamespacedKey
     * @return true 表示阻止
     */
    boolean isBlocked(@NotNull ItemStack item, @NotNull NamespacedKey recipeKey);
}
