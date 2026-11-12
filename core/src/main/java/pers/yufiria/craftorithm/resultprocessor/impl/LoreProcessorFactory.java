package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.chat.BukkitTextProcessor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.ArrayList;
import java.util.List;

public enum LoreProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "lore";

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void declareBehaviors(BehaviorRegistrar registrar) {
        registrar.behavior(Behaviors.COPY_FROM_SOURCE, (context, data) -> copyFromSource())
            .behavior(Behaviors.ADD, (context, data) -> add(data))
            .behavior(Behaviors.SET, (context, data) -> set(data))
            .behavior(Behaviors.MERGE_SOURCE, (context, data) -> mergeSource())
            .behavior(Behaviors.REMOVE, (context, data) -> remove(data));
    }

    private ResultProcessor copyFromSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/copy_from_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            if (!sourceMeta.hasLore()) {
                return;
            }
            List<String> sourceLore = sourceMeta.getLore();
            resultItem.edit(meta -> meta.setLore(sourceLore));
        });
    }

    /**
     * 合并为结果 lore 在前，源 lore 追加在后
     */
    private ResultProcessor mergeSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/merge_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            if (!sourceMeta.hasLore()) {
                return;
            }
            List<String> sourceLore = sourceMeta.getLore();
            resultItem.edit(meta -> {
                if (!meta.hasLore()) {
                    meta.setLore(sourceLore);
                    return;
                }
                List<String> merged = new ArrayList<>(meta.getLore());
                merged.addAll(sourceLore);
                meta.setLore(merged);
            });
        });
    }

    /**
     * 追加到结果物品已有的 lore 之后
     */
    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<ValueProvider<String>> lines = parseValueProviders(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/add", (sourceItem, resultItem, player) -> {
            if (lines.isEmpty()) {
                return;
            }
            List<String> parsed = parseLines(lines, sourceItem, resultItem, player);
            resultItem.edit(meta -> {
                List<String> merged = new ArrayList<>();
                if (meta.hasLore()) {
                    merged.addAll(meta.getLore());
                }
                merged.addAll(parsed);
                meta.setLore(merged);
            });
        });
    }

    /**
     * 整体替换结果物品的 lore
     */
    private ResultProcessor set(@Nullable ConfigurationSection data) {
        List<ValueProvider<String>> lines = parseValueProviders(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/set", (sourceItem, resultItem, player) -> {
            List<String> parsed = parseLines(lines, sourceItem, resultItem, player);
            if (parsed.isEmpty() && !lines.isEmpty()) {
                //所有行都求值失败时不要把原有 lore 清空
                return;
            }
            resultItem.edit(meta -> meta.setLore(parsed.isEmpty() ? null : parsed));
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
        @Nullable ItemWrapper sourceItem,
        ItemWrapper resultItem,
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
        List<String> targets = parseRemoveTargets(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/remove", (sourceItem, resultItem, player) -> {
            if (targets == null) {
                resultItem.edit(meta -> meta.setLore(null));
                return;
            }
            resultItem.edit(meta -> {
                if (!meta.hasLore()) {
                    return;
                }
                List<String> current = new ArrayList<>(meta.getLore());
                current.removeAll(targets);
                meta.setLore(current.isEmpty() ? null : current);
            });
        });
    }

    /**
     * @return null 表示没有写 data, 即清空全部
     */
    private @Nullable List<String> parseRemoveTargets(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            return null;
        }
        return List.copyOf(data.getStringList("value"));
    }

}
