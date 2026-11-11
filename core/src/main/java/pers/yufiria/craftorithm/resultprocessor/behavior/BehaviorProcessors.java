package pers.yufiria.craftorithm.resultprocessor.behavior;

import crypticlib.util.TriConsumer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

final class BehaviorProcessors {

    static ResultProcessor of(String name, TriConsumer<ItemWrapper, ItemWrapper, Player> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player) {
                action.accept(sourceItem, resultItem, player);
            }
        };
    }

    /**
     * 源物品不存在时直接跳过, 不抛异常
     */
    static ResultProcessor requireSource(String name, TriConsumer<ItemWrapper, ItemWrapper, Player> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player) {
                if (sourceItem == null) {
                    return;
                }
                action.accept(sourceItem, resultItem, player);
            }
        };
    }

}
