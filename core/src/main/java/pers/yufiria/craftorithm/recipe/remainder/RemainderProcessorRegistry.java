package pers.yufiria.craftorithm.recipe.remainder;

import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.remainder.impl.DamageRemainderProcessor;
import pers.yufiria.craftorithm.recipe.remainder.impl.KeepRemainderProcessor;
import pers.yufiria.craftorithm.recipe.remainder.impl.ReplaceRemainderProcessor;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * remainders 处理器类型注册表
 * <p>
 * 内置 {@code damage}/{@code replace}/{@code keep} 三种类型。其他插件可通过 {@link #register} 注册自定义类型，
 * 之后即可在配方配置的 remainders 中使用对应的 type 值
 * <pre>{@code
 * RemainderProcessorRegistry.INSTANCE.register("my_type", (section, recipeKey) -> {
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
public enum RemainderProcessorRegistry {

    INSTANCE;

    /**
     * 处理器工厂, 从单条配置创建处理器
     */
    @FunctionalInterface
    public interface Factory {

        /**
         * @return 配置无效时返回null, 并自行输出警告
         */
        @Nullable RemainderProcessor create(@NotNull ConfigurationSection section, @NotNull NamespacedKey recipeKey);

    }

    private final Map<String, Factory> factories = new ConcurrentHashMap<>();

    RemainderProcessorRegistry() {
        register(RemainderProcessor.TYPE_DAMAGE, DamageRemainderProcessor::fromConfig);
        register(RemainderProcessor.TYPE_REPLACE, ReplaceRemainderProcessor::fromConfig);
        register(RemainderProcessor.TYPE_KEEP, (section, recipeKey) -> KeepRemainderProcessor.INSTANCE);
    }

    /**
     * 注册一个剩余物类型, 同名类型会被覆盖
     *
     * @param type    配置中的 type 值, 不区分大小写
     * @param factory 处理器工厂
     */
    public void register(@NotNull String type, @NotNull Factory factory) {
        factories.put(normalize(type), factory);
    }

    /**
     * 注销一个剩余物类型
     *
     * @return 之前是否存在
     */
    public boolean unregister(@NotNull String type) {
        return factories.remove(normalize(type)) != null;
    }

    public boolean isRegistered(@NotNull String type) {
        return factories.containsKey(normalize(type));
    }

    /**
     * 按配置中的 type 创建处理器
     *
     * @return 类型未注册或配置无效时返回null
     */
    public @Nullable RemainderProcessor create(@NotNull ConfigurationSection section, @NotNull NamespacedKey recipeKey) {
        String type = section.getString("type");
        if (type == null || type.isBlank()) {
            CrypticLib.warn("Missing remainder type in recipe: " + recipeKey);
            return null;
        }
        Factory factory = factories.get(normalize(type));
        if (factory == null) {
            CrypticLib.warn("Unknown remainder type " + type + " in recipe: " + recipeKey);
            return null;
        }
        return factory.create(section, recipeKey);
    }

    private String normalize(String type) {
        return type.toLowerCase(Locale.ROOT);
    }

}
