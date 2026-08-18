package pers.yufiria.craftorithm.item.ingredientrestriction;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ingredientrestriction.impl.ItemIdRestrictionRule;
import pers.yufiria.craftorithm.item.ingredientrestriction.impl.LoreRestrictionRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 合成限制规则注册中心
 * <p>
 * 第三方插件应在服务器完全启动之前调用 {@link #register(IngredientRestrictionRuleFactory)} 注册自定义规则
 */
public enum IngredientRestrictionRegistry {

    INSTANCE;

    private final Map<String, IngredientRestrictionRuleFactory> factories = new ConcurrentHashMap<>();

    IngredientRestrictionRegistry() {
        register(LoreRestrictionRule.FACTORY);
        register(ItemIdRestrictionRule.FACTORY);
    }

    /**
     * 注册规则工厂
     *
     * @param factory 规则工厂
     */
    public void register(@NotNull IngredientRestrictionRuleFactory factory) {
        factories.put(factory.type(), factory);
    }

    /**
     * 移除规则工厂
     *
     * @param type 规则类型标识
     * @return 被移除的工厂，不存在返回 null
     */
    public @Nullable IngredientRestrictionRuleFactory unregister(@NotNull String type) {
        return factories.remove(type);
    }

    /**
     * 根据配置节点创建规则实例
     *
     * @param section 包含 type 字段的配置节点
     * @return 规则实例，type 未知返回 null
     */
    public @Nullable IngredientRestrictionRule create(@NotNull ConfigurationSection section) {
        String type = section.getString("type", "");
        IngredientRestrictionRuleFactory factory = factories.get(type);
        if (factory == null) {
            return null;
        }
        return factory.load(section);
    }

    /**
     * 获取所有已注册的规则类型
     */
    public @NotNull Map<String, IngredientRestrictionRuleFactory> factories() {
        return Map.copyOf(factories);
    }
}
