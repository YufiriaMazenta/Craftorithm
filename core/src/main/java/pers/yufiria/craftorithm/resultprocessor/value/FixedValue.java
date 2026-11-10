package pers.yufiria.craftorithm.resultprocessor.value;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 固定值, 不涉及脚本与上下文
 */
public record FixedValue<T>(T value) implements ValueProvider<T> {

    @Override
    public @NotNull T resolve(@Nullable Player player, @Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
        return value;
    }

}
