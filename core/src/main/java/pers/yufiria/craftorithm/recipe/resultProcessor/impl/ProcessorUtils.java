package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.util.function.BiConsumer;

/**
 * 结果处理器工厂的公共构建工具
 */
final class ProcessorUtils {

    private ProcessorUtils() {
    }

    /**
     * 创建普通 processor，不要求 sourceItem 存在
     */
    static ResultProcessor processor(String name, BiConsumer<ItemStack, ItemStack> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                action.accept(sourceItem, resultItem);
            }
        };
    }

    /**
     * 创建需要 sourceItem 不为 null 的 processor。
     * 当 sourceItem 为 null 时（如工作台配方），直接跳过，不抛异常。
     */
    static ResultProcessor processorRequireSource(String name, BiConsumer<ItemStack, ItemStack> action) {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return name;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem) {
                if (sourceItem == null) return;
                action.accept(sourceItem, resultItem);
            }
        };
    }

    static ResultProcessor unsupported(String component, ProcessingStrategy strategy) {
        throw new UnsupportedOperationException(component + " does not support " + strategy);
    }

}
