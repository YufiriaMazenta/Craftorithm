package pers.yufiria.craftorithm.resultprocessor.behavior;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

/**
 * 字段行为的实现。
 * <p>
 * 与 {@link ProcessorBehavior} 的区别只是多了一个字段绑定:
 * 声明在 {@code valueType(...)} 之后的行为一定作用在这个字段上, 不需要再从
 * {@link BehaviorContext#field()} 里取。
 */
@FunctionalInterface
public interface FieldBehavior<T> {

    /**
     * 返回 null 表示该配置下不可用, 由调用方跳过这一条
     */
    @Nullable
    ResultProcessor create(
        BehaviorContext context,
        FieldBinding<T> field,
        @Nullable ConfigurationSection data
    );

}
