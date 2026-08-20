package pers.yufiria.craftorithm.recipe.resultProcessor.impl;

import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.util.ArrayList;
import java.util.List;

import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processor;
import static pers.yufiria.craftorithm.recipe.resultProcessor.impl.ProcessorUtils.processorRequireSource;

public class LoreProcessorFactory implements ComponentProcessorFactory {

    public static final LoreProcessorFactory INSTANCE = new LoreProcessorFactory();

    public static final String COMPONENT_NAME = "lore";

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

    private static ResultProcessor copyFromSource() {
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
    private static ResultProcessor mergeSource() {
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

    private static ResultProcessor add(@Nullable ConfigurationSection data) {
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

    private static ResultProcessor remove(@Nullable ConfigurationSection data) {
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
