package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 一个字段的声明器。
 * <p>
 * 读、写、从配置取值分开声明, 每一样都是可选地、按名字追加, 不依赖参数顺序:
 * <pre>{@code
 * registrar.valueType(String.class)
 *          .getter(meta -> meta.hasDisplayName() ? meta.getDisplayName() : null)
 *          .setter((meta, value) -> meta.setDisplayName(value))
 *          .valueReader(data -> ValueProviders.text(data, "value", "", "display_name/value"))
 *          .behavior(Behaviors.ADD, ScalarBehaviors.add());
 * }</pre>
 * {@code getter} 与 {@code setter} 必须在声明行为之前给出。
 *
 * @see ScalarBehaviors 内置标准行为的实现
 */
public interface FieldDeclaration<T> {

    /**
     * 读取 meta 上的字段, 没有该字段时返回 null
     */
    FieldDeclaration<T> getter(@NotNull Function<ItemMeta, T> getter);

    /**
     * 写入 meta 上的字段, value 为 null 表示清除
     */
    FieldDeclaration<T> setter(@NotNull BiConsumer<ItemMeta, T> setter);

    /**
     * 把配置节解析成取值来源(固定值或脚本), 决定 {@code add} 类行为的 value 从哪来。
     * <p>
     * 不声明时该字段不支持从配置取值, 只能从源物品复制。
     *
     * @param valueReader 把配置节解析成取值来源, 不支持时返回 null
     */
    FieldDeclaration<T> valueReader(@NotNull Function<ConfigurationSection, ValueProvider<T>> valueReader);

    /**
     * 限定该字段能写在哪些 meta 上, 例如耐久值只能写在 Damageable 上。
     * <p>
     * 不满足时不写入并告警一次。
     *
     * @param acceptance 判断某个 meta 是否接受该字段
     */
    FieldDeclaration<T> accepts(@NotNull Predicate<ItemMeta> acceptance);

    /**
     * 为这个字段注册一个行为
     *
     * @param key      配置里的 {@code type}, 使用 {@link Behaviors} 里的常量或第三方自定义名字
     * @param behavior 行为实现
     */
    FieldDeclaration<T> behavior(String key, FieldBehavior<T> behavior);

}
