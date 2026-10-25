package pers.yufiria.craftorithm.recipe.remainder;

import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.remainder.impl.DamageRemainderAction;
import pers.yufiria.craftorithm.recipe.remainder.impl.KeepRemainderAction;
import pers.yufiria.craftorithm.recipe.remainder.impl.ReplaceRemainderAction;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * remainders 动作注册表
 * <p>
 * 内置 {@code damage}/{@code replace}/{@code keep} 三种动作。其他插件可通过 {@link #register} 注册自定义动作，
 * 之后即可在配方配置的 remainders 中使用对应的 action 值
 * <pre>{@code
 * RemainderActionRegistry.INSTANCE.register("my_action", (section, recipeKey) -> {
 *     String arg = section.getString("my_arg");
 *     if (arg == null) {
 *         return null; // 配置无效, 规则会被跳过
 *     }
 *     return source -> {
 *         ItemStack remainder = source.clone();
 *         // 计算剩余物
 *         return remainder;
 *     };
 * });
 * }</pre>
 */
public enum RemainderActionRegistry {

    INSTANCE;

    private final Map<String, RemainderAction.Factory> factories = new ConcurrentHashMap<>();

    RemainderActionRegistry() {
        register(DamageRemainderAction.KEY, DamageRemainderAction::fromConfig);
        register(ReplaceRemainderAction.KEY, ReplaceRemainderAction::fromConfig);
        register(KeepRemainderAction.KEY, (section, recipeKey) -> KeepRemainderAction.INSTANCE);
    }

    /**
     * 注册一个剩余物处理动作, 同名动作会被覆盖
     *
     * @param action  配置中的 action 值, 不区分大小写
     * @param factory 动作工厂
     */
    public void register(@NotNull String action, @NotNull RemainderAction.Factory factory) {
        factories.put(normalize(action), factory);
    }

    /**
     * 注销一个剩余物处理动作
     *
     * @return 之前是否存在
     */
    public boolean unregister(@NotNull String action) {
        return factories.remove(normalize(action)) != null;
    }

    public boolean isRegistered(@NotNull String action) {
        return factories.containsKey(normalize(action));
    }

    /**
     * 按配置中的 action 创建动作
     *
     * @return 动作未注册或配置无效时返回null
     */
    public @Nullable RemainderAction create(@NotNull ConfigurationSection section, @NotNull NamespacedKey recipeKey) {
        String action = section.getString("action");
        if (action == null || action.isBlank()) {
            CrypticLib.warn("Missing remainder action in recipe: " + recipeKey);
            return null;
        }
        RemainderAction.Factory factory = factories.get(normalize(action));
        if (factory == null) {
            CrypticLib.warn("Unknown remainder action " + action + " in recipe: " + recipeKey);
            return null;
        }
        return factory.create(section, recipeKey);
    }

    private String normalize(String action) {
        return action.toLowerCase(Locale.ROOT);
    }

}
