package pers.yufiria.craftorithm.resultprocessor.value;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;

/**
 * 结果处理器的取值来源: 固定值, 或者一段在合成期执行的脚本
 * <p>
 * 脚本求值失败时返回 null, 由调用方跳过该值(列表型组件只跳过当前条目),
 * 这样一条坏脚本不会导致整个处理动作失效
 */
@FunctionalInterface
public interface ValueProvider<T> {

    @Nullable
    T resolve(@Nullable Player player, @Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem);

}
