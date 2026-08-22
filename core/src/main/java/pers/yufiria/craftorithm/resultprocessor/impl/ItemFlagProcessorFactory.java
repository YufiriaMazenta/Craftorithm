package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.processorRequireSource;

public enum ItemFlagProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "item_flag";

    private final Map<String, Function<ConfigurationSection, ResultProcessor>> handlers = new HashMap<>();

    ItemFlagProcessorFactory() {
        handlers.put("copy_from_source", data -> copyFromSource());
        handlers.put("add", this::add);
        handlers.put("merge_source", data -> mergeSource());
        handlers.put("remove", this::remove);
    }

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = handlers.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(COMPONENT_NAME + " does not support type: " + type);
        }
        return handler.apply(data);
    }

    public Map<String, Function<ConfigurationSection, ResultProcessor>> handlers() {
        return handlers;
    }

    /**
     * 复制为完全覆盖，先清空结果已有的 flag
     */
    private ResultProcessor copyFromSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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
    private ResultProcessor mergeSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<String> flagNames = data != null ? data.getStringList("value") : List.of();
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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

    private ResultProcessor remove(@Nullable ConfigurationSection data) {
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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
    private ItemFlag parseItemFlag(String flagName) {
        try {
            return ItemFlag.valueOf(flagName.toUpperCase());
        } catch (IllegalArgumentException e) {
            CrypticLib.info("&eUnknown ItemFlag: " + flagName);
            return null;
        }
    }

}
