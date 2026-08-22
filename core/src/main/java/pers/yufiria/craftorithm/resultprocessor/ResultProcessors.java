package pers.yufiria.craftorithm.resultprocessor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class ResultProcessors {

    private final List<ResultProcessor> processors;

    public ResultProcessors(List<ResultProcessor> processors) {
        this.processors = processors;
    }

    public void processItem(@Nullable ItemStack sourceItem, ItemStack resultItem, @Nullable Player player) {
        for (ResultProcessor processor : processors) {
            processor.processItem(sourceItem, resultItem, player);
        }
    }

}
