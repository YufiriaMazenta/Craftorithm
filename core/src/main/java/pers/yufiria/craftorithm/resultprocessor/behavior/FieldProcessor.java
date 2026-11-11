package pers.yufiria.craftorithm.resultprocessor.behavior;

import crypticlib.CrypticLib;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

/**
 * 字段型处理器的统一实现。
 * <p>
 * 只跟物品打交道: 读字段、算值、写字段都通过 {@link FieldBinding} 完成,
 * meta 由物品缓存, 这里不持有任何 meta 快照。
 */
final class FieldProcessor<T> implements ResultProcessor {

    private final FieldBinding<T> field;
    private final String processorName;
    private final @Nullable FieldBinding.Action<T> action;

    FieldProcessor(FieldBinding<T> field, String processorName, @Nullable FieldBinding.Action<T> action) {
        this.field = field;
        this.processorName = processorName;
        this.action = action;
    }

    @Override
    public String processorName() {
        return processorName;
    }

    @Override
    public void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player) {
        if (action == null) {
            //清除时静默跳过不适用的物品, 否则每次合成都刷一条告警
            if (field.accepts(resultItem)) {
                field.set(resultItem, null);
            }
            return;
        }
        if (!field.accepts(resultItem)) {
            CrypticLib.warn(processorName + ": " + resultItem.type() + " does not support this component, skipped");
            return;
        }
        T value = action.resolve(sourceItem, resultItem, player);
        if (value != null) {
            field.set(resultItem, value);
        }
    }

}
