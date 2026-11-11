package pers.yufiria.craftorithm.resultprocessor.behavior;

import crypticlib.CrypticLib;
import org.bukkit.entity.Player;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

import java.util.function.BiFunction;

/**
 * 内置的标准字段行为, 所有字段组件共用。
 */
public final class ScalarBehaviors {

    /**
     * 从源物品复制该字段
     */
    public static <T> FieldBehavior<T> copyFromSource() {
        return (context, field, data) -> field.processor(
            context.qualifiedName(),
            (sourceItem, resultItem, player) -> sourceItem == null
                ? null
                : field.get(sourceItem)
        );
    }

    /**
     * 写入配置里的值
     */
    public static <T> FieldBehavior<T> add() {
        return add((value, player) -> value);
    }

    /**
     * @param transform 写入前的运行期修正(钳制/文本处理等), 返回 null 表示本次不写入
     */
    public static <T> FieldBehavior<T> add(BiFunction<T, Player, T> transform) {
        return (context, field, data) -> {
            ValueProvider<T> value = field.valueReader(data);
            if (value == null) {
                return null;
            }
            return field.processor(
                context.qualifiedName(),
                (sourceItem, resultItem, player) -> {
                    T resolved = value.resolve(player, sourceItem, resultItem);
                    return resolved == null ? null : transform.apply(resolved, player);
                }
            );
        };
    }

    /**
     * 与 {@link #add(BiFunction)} 相同, 但配置里必须有 value, 缺失时告警并跳过
     */
    public static <T> FieldBehavior<T> addRequired(BiFunction<T, Player, T> transform) {
        return (context, field, data) -> {
            if (data == null || data.get("value") == null) {
                CrypticLib.warn(context.qualifiedName() + ": missing 'value'");
                return field.processor(context.qualifiedName(), (sourceItem, resultItem, player) -> null);
            }
            return add(transform).create(context, field, data);
        };
    }

    /**
     * 清除该字段
     */
    public static <T> FieldBehavior<T> remove() {
        return (context, field, data) -> field.clearProcessor(context.qualifiedName());
    }

}
