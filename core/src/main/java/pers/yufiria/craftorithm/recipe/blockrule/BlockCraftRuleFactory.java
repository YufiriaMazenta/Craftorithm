package pers.yufiria.craftorithm.recipe.blockrule;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * 合成限制规则工厂，从配置节点创建规则实例
 */
@FunctionalInterface
public interface BlockCraftRuleFactory {

    /**
     * 从配置节点加载规则实例
     *
     * @param section 配置节点
     * @return 规则实例
     */
    @NotNull BlockCraftRule load(@NotNull ConfigurationSection section);
}
