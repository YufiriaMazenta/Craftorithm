package pers.yufiria.craftorithm.resultprocessor;

import crypticlib.CrypticLib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResultProcessors {

    private final List<ResultProcessor> processors;

    public ResultProcessors(List<ResultProcessor> processors) {
        this.processors = processors;
    }

    public void processItem(@Nullable ItemStack sourceItem, ItemStack resultItem, @Nullable Player player) {
        for (ResultProcessor processor : processors) {
            try {
                processor.processItem(sourceItem, resultItem, player);
            } catch (Throwable throwable) {
                //单个组件处理失败不能中断其余组件, 否则一个坏配置会让产物整体少组件
                CrypticLib.error("Failed to apply result processor '" + processor.processorName() + "'", throwable);
            }
        }
    }

}
