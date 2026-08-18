package pers.yufiria.craftorithm.item.ingredientrestriction;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * 合成限制规则工厂，从配置节点创建规则实例
 */
public interface IngredientRestrictionRuleFactory {

    @NotNull String type();

    /**
     * 从配置节点加载规则实例
     *
     * @param section 配置节点
     * @return 规则实例
     */
    @NotNull IngredientRestrictionRule load(@NotNull ConfigurationSection section);
}
