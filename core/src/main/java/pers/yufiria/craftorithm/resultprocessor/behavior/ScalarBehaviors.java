package pers.yufiria.craftorithm.resultprocessor.behavior;

import crypticlib.CrypticLib;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 内置的标准行为。
 * <p>
 * 读写方式由调用方给出: 读用 {@code getter}, 写用 {@code setter},
 * 配置里的值用 {@code valueReader}。
 */
public final class ScalarBehaviors {

    /**
     * 从源物品复制, getter 返回 null 表示源物品没有这个值
     */
    public static <T> ProcessorBehavior copyFromSource(
        Function<ItemMeta, T> getter,
        BiConsumer<ItemMeta, T> setter
    ) {
        return (context, data) -> context.requireSource((sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            if (sourceMeta == null) {
                return;
            }
            T value = getter.apply(sourceMeta);
            if (value != null) {
                resultItem.edit(meta -> setter.accept(meta, value));
            }
        });
    }

    /**
     * 写入配置里的值
     */
    public static <T> ProcessorBehavior add(
        BiConsumer<ItemMeta, T> setter,
        Function<ConfigurationSection, ValueProvider<T>> valueReader
    ) {
        return add(setter, valueReader, (value, player) -> value);
    }

    /**
     * @param transform 写入前的运行期修正(钳制/文本处理等), 返回 null 表示本次不写入
     */
    public static <T> ProcessorBehavior add(
        BiConsumer<ItemMeta, T> setter,
        Function<ConfigurationSection, ValueProvider<T>> valueReader,
        BiFunction<T, Player, T> transform
    ) {
        return (context, data) -> {
            ValueProvider<T> value = valueReader.apply(data);
            if (value == null) {
                return null;
            }
            return context.processor((sourceItem, resultItem, player) -> {
                T resolved = value.resolve(player, sourceItem, resultItem);
                if (resolved == null) {
                    return;
                }
                T transformed = transform.apply(resolved, player);
                if (transformed != null) {
                    resultItem.edit(meta -> setter.accept(meta, transformed));
                }
            });
        };
    }

    /**
     * 与 {@link #add(BiConsumer, Function, BiFunction)} 相同, 但配置里必须有 value, 缺失时告警并跳过
     */
    public static <T> ProcessorBehavior addRequired(
        BiConsumer<ItemMeta, T> setter,
        Function<ConfigurationSection, ValueProvider<T>> valueReader,
        BiFunction<T, Player, T> transform
    ) {
        return (context, data) -> {
            if (data == null || data.get("value") == null) {
                CrypticLib.warn(context.qualifiedName() + ": missing 'value'");
                return context.processor((sourceItem, resultItem, player) -> {
                });
            }
            return add(setter, valueReader, transform).create(context, data);
        };
    }

    /**
     * 清除该值, setter 收到 null
     */
    public static <T> ProcessorBehavior remove(BiConsumer<ItemMeta, T> setter) {
        return (context, data) -> context.processor((sourceItem, resultItem, player) ->
            resultItem.edit(meta -> setter.accept(meta, null))
        );
    }

}
