package pers.yufiria.craftorithm.recipe.blockrule;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.blockrule.impl.ItemIdBlockCraftRule;
import pers.yufiria.craftorithm.recipe.blockrule.impl.LoreBlockCraftRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 合成限制规则注册中心
 * <p>
 * 第三方插件应在服务器完全启动之前调用 {@link #register(String, BlockCraftRuleFactory)} 注册自定义规则
 */
public enum BlockCraftRuleRegistry {

    INSTANCE;

    private final Map<String, BlockCraftRuleFactory> factories = new ConcurrentHashMap<>();

    BlockCraftRuleRegistry() {
        register("lore", LoreBlockCraftRule.FACTORY);
        register("item_id", ItemIdBlockCraftRule.FACTORY);
    }

    /**
     * 注册规则工厂
     *
     * @param type    规则类型标识（对应配置中的 type 字段）
     * @param factory 规则工厂
     */
    public void register(@NotNull String type, @NotNull BlockCraftRuleFactory factory) {
        factories.put(type, factory);
    }

    /**
     * 移除规则工厂
     *
     * @param type 规则类型标识
     * @return 被移除的工厂，不存在返回 null
     */
    public @Nullable BlockCraftRuleFactory unregister(@NotNull String type) {
        return factories.remove(type);
    }

    /**
     * 根据配置节点创建规则实例
     *
     * @param section 包含 type 字段的配置节点
     * @return 规则实例，type 未知返回 null
     */
    public @Nullable BlockCraftRule create(@NotNull ConfigurationSection section) {
        String type = section.getString("type", "");
        BlockCraftRuleFactory factory = factories.get(type);
        if (factory == null) {
            return null;
        }
        return factory.load(section);
    }

    /**
     * 获取所有已注册的规则类型
     */
    public @NotNull Map<String, BlockCraftRuleFactory> factories() {
        return Map.copyOf(factories);
    }
}
