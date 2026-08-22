package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.resultprocessor.impl.ProcessorUtils.processorRequireSource;

public enum LoreProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "lore";

    private final Map<String, Function<ConfigurationSection, ResultProcessor>> handlers = new HashMap<>();

    LoreProcessorFactory() {
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

    private ResultProcessor copyFromSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasLore()) {
                resultMeta.setLore(sourceMeta.getLore());
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    /**
     * 合并为结果 lore 在前，源 lore 追加在后
     */
    private ResultProcessor mergeSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasLore()) {
                List<String> sourceLore = sourceMeta.getLore();
                if (resultMeta.hasLore()) {
                    List<String> merged = new ArrayList<>(resultMeta.getLore());
                    merged.addAll(sourceLore);
                    resultMeta.setLore(merged);
                } else {
                    resultMeta.setLore(sourceLore);
                }
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<String> rawLines = data != null ? data.getStringList("value") : List.of();
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            List<String> processed = rawLines.stream()
                .map(line -> {
                    String parsed = player != null ? BukkitTextProcessor.placeholder(player, line) : line;
                    return BukkitTextProcessor.color(parsed);
                })
                .toList();
            resultMeta.setLore(processed);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private ResultProcessor remove(@Nullable ConfigurationSection data) {
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (data == null || data.getKeys(false).isEmpty()) {
                resultMeta.setLore(null);
            } else {
                List<String> removeList = data.getStringList("value");
                if (resultMeta.hasLore()) {
                    List<String> current = new ArrayList<>(resultMeta.getLore());
                    current.removeAll(removeList);
                    resultMeta.setLore(current.isEmpty() ? null : current);
                }
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

}
