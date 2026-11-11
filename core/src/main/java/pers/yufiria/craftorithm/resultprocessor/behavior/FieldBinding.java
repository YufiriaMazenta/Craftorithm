package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;

/**
 * 组件的一个可读写字段。
 * <p>
 * 把"读字段/写字段/怎么从配置取值"收在一处, 标准行为
 * ({@code copy_from_source}、{@code add}、{@code remove})才能被所有标量组件复用。
 * <p>
 * 字段本身由 {@link FieldDeclaration} 声明, 这里只暴露运行期的读写。
 * 读写都走 {@link ItemWrapper}: meta 由物品自己缓存, 一条处理链只取一次、只写回一次。
 */
public interface FieldBinding<T> {

    /**
     * 字段的取值类型, 供 {@link BehaviorContext#field()} 的调用方校验
     */
    Class<T> valueType();

    /**
     * 读取物品上的字段, 字段不存在或类型不匹配时返回 null
     */
    @Nullable
    T get(@NotNull ItemWrapper item);

    /**
     * 写入物品上的字段, value 为 null 表示清除
     */
    void set(@NotNull ItemWrapper item, @Nullable T value);

    /**
     * 用声明期给出的取值方式把配置解析成取值来源, 该字段不支持从配置取值时返回 null
     */
    @Nullable
    ValueProvider<T> valueReader(@Nullable ConfigurationSection data);

    /**
     * 该字段能否写在物品上, 例如耐久值只能写在 Damageable 上。
     * <p>
     * 返回 false 时不写入并告警一次。
     */
    default boolean accepts(@NotNull ItemWrapper item) {
        return true;
    }

    /**
     * 由该字段生成一个处理器: 算出的值非 null 才写回, 避免"求值失败"被当成"清空"
     */
    default ResultProcessor processor(@NotNull String processorName, @NotNull Action<T> action) {
        return new FieldProcessor<>(this, processorName, action);
    }

    /**
     * 由该字段生成一个清除处理器
     */
    default ResultProcessor clearProcessor(@NotNull String processorName) {
        return new FieldProcessor<>(this, processorName, null);
    }

    /**
     * 处理器运行期的字段计算逻辑, 返回 null 表示本次不写入。
     * <p>
     * 参数与 {@link BehaviorContext#processor} 一致: 源物品(只读)、结果物品、玩家。
     * 只算值, 写回由框架做。
     */
    @FunctionalInterface
    interface Action<T> {

        @Nullable
        T resolve(
            @Nullable ItemWrapper sourceItem,
            @NotNull ItemWrapper resultItem,
            @Nullable Player player
        );

    }

}
