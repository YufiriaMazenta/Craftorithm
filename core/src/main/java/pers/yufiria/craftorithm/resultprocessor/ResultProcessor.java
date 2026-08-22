package pers.yufiria.craftorithm.resultprocessor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResultProcessor {

    String processorName();

    /**
     * 对输入物品和结果的ItemStack进行处理
     *
     * @param sourceItem 输入/源物品（锻造台/铁砧有base，工作台配方为null）
     * @param resultItem 结果物品
     */
    void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player);

}
