package pers.yufiria.craftorithm.resultprocessor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ResultProcessor {

    String processorName();

    /**
     * 对输入物品和结果物品进行处理
     *
     * @param sourceItem 输入/源物品(锻造台/铁砧有base, 工作台配方为null), 只读包装
     * @param resultItem 结果物品草稿, 改动由调用方在处理链结束时统一写回
     */
    void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player);

}
