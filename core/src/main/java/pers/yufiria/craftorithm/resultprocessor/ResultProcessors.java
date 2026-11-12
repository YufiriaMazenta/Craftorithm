package pers.yufiria.craftorithm.resultprocessor;

import crypticlib.CrypticLib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 一个配方的处理器链: 按配置顺序执行, 单个处理器失败不影响其余。
 */
public class ResultProcessors {

    private final List<ResultProcessor> processors;

    public ResultProcessors(List<ResultProcessor> processors) {
        this.processors = processors;
    }

    public void processItem(@Nullable ItemStack sourceItem, ItemStack resultItem, @Nullable Player player) {
        //源物品只读: 它的包装不会写回, 多个 copy 类行为共用同一份 meta
        ItemWrapper cachedSource = sourceItem == null ? null : ItemWrapper.readOnly(sourceItem);
        ItemWrapper itemWrapper = ItemWrapper.of(resultItem);
        try {
            for (ResultProcessor processor : processors) {
                try {
                    processor.processItem(cachedSource, itemWrapper, player);
                } catch (Throwable throwable) {
                    //单个组件处理失败不能中断其余组件, 否则一个坏配置会让产物整体少组件
                    CrypticLib.error("Failed to apply result processor '" + processor.processorName() + "'", throwable);
                }
            }
        } finally {
            //中途抛异常时, 已经做过的改动同样要落回物品
            itemWrapper.apply();
        }
    }

}
