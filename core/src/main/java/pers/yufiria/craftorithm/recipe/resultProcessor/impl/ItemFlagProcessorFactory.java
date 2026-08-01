package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import crypticlib.util.IOHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.util.List;

import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processorRequireSource;

public class ItemFlagProcessorFactory implements ComponentProcessorFactory {

    public static final ItemFlagProcessorFactory INSTANCE = new ItemFlagProcessorFactory();

    public static final String COMPONENT_NAME = "item_flag";

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
        return switch (strategy) {
            case COPY_FROM_SOURCE -> copyFromSource();
            case ADD -> add(data);
            case MERGE_SOURCE -> mergeSource();
            case REMOVE -> remove(data);
        };
    }

    /**
     * 复制为完全覆盖，先清空结果已有的 flag
     */
    private static ResultProcessor copyFromSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            resultMeta.removeItemFlags(ItemFlag.values());
            sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags);
            resultItem.setItemMeta(resultMeta);
        });
    }

    /**
     * 合并保留结果已有的 flag，仅追加源的 flag
     */
    private static ResultProcessor mergeSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor add(@Nullable ConfigurationSection data) {
        List<String> flagNames = data != null ? data.getStringList("value") : List.of();
        return processor(COMPONENT_NAME, (sourceItem, resultItem) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            for (String flagName : flagNames) {
                ItemFlag flag = parseItemFlag(flagName);
                if (flag != null) {
                    resultMeta.addItemFlags(flag);
                }
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor remove(@Nullable ConfigurationSection data) {
        return processor(COMPONENT_NAME, (sourceItem, resultItem) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (data == null || data.getKeys(false).isEmpty()) {
                resultMeta.removeItemFlags(ItemFlag.values());
            } else {
                List<String> removeList = data.getStringList("value");
                for (String flagName : removeList) {
                    ItemFlag flag = parseItemFlag(flagName);
                    if (flag != null) {
                        resultMeta.removeItemFlags(flag);
                    }
                }
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    @Nullable
    private static ItemFlag parseItemFlag(String flagName) {
        try {
            return ItemFlag.valueOf(flagName.toUpperCase());
        } catch (IllegalArgumentException e) {
            IOHelper.info("&eUnknown ItemFlag: " + flagName);
            return null;
        }
    }

}
