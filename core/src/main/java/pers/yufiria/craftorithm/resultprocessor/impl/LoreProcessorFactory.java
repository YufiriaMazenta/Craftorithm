package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum LoreProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "lore";

    private final Map<String, Function<ConfigurationSection, ResultProcessor>> handlers = new HashMap<>();

    LoreProcessorFactory() {
        handlers.put("copy_from_source", data -> copyFromSource());
        handlers.put("add", this::add);
        handlers.put("set", this::set);
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
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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

    /**
     * 追加到结果物品已有的 lore 之后
     */
    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<ValueProvider<String>> lines = parseValueProviders(data);
        return ProcessorUtils.processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            if (lines.isEmpty()) {
                return;
            }
            ItemMeta resultMeta = resultItem.getItemMeta();
            List<String> merged = new ArrayList<>();
            if (resultMeta.hasLore()) {
                merged.addAll(resultMeta.getLore());
            }
            merged.addAll(parseLines(lines, sourceItem, resultItem, player));
            resultMeta.setLore(merged);
            resultItem.setItemMeta(resultMeta);
        });
    }

    /**
     * 整体替换结果物品的 lore
     */
    private ResultProcessor set(@Nullable ConfigurationSection data) {
        List<ValueProvider<String>> lines = parseValueProviders(data);
        return ProcessorUtils.processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            List<String> parsed = parseLines(lines, sourceItem, resultItem, player);
            if (parsed.isEmpty() && !lines.isEmpty()) {
                //所有行都求值失败时不要把原有 lore 清空
                return;
            }
            resultMeta.setLore(parsed.isEmpty() ? null : parsed);
            resultItem.setItemMeta(resultMeta);
        });
    }

    private List<ValueProvider<String>> parseValueProviders(@Nullable ConfigurationSection data) {
        if (data == null) {
            return List.of();
        }
        List<String> rawLines = data.getStringList("value");
        List<ValueProvider<String>> providers = new ArrayList<>(rawLines.size());
        for (String line : rawLines) {
            providers.add(ValueProviders.textValue(line, line, COMPONENT_NAME + "/value"));
        }
        return providers;
    }

    private List<String> parseLines(
        List<ValueProvider<String>> lines,
        @Nullable ItemStack sourceItem,
        ItemStack resultItem,
        @Nullable Player player
    ) {
        List<String> parsed = new ArrayList<>(lines.size());
        for (ValueProvider<String> line : lines) {
            String text = line.resolve(player, sourceItem, resultItem);
            if (text == null) {
                //脚本求值失败: 只跳过这一行
                continue;
            }
            String withPlaceholders = player != null ? BukkitTextProcessor.placeholder(player, text) : text;
            parsed.add(BukkitTextProcessor.color(withPlaceholders));
        }
        return parsed;
    }

    private ResultProcessor remove(@Nullable ConfigurationSection data) {
        return ProcessorUtils.processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
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
