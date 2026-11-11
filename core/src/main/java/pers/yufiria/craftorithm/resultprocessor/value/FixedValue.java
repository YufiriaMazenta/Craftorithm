package pers.yufiria.craftorithm.resultprocessor.value;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;

/**
 * 固定值, 不涉及脚本与上下文
 */
public record FixedValue<T>(T value) implements ValueProvider<T> {

    @Override
    public @NotNull T resolve(@Nullable Player player, @Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem) {
        return value;
    }

}
