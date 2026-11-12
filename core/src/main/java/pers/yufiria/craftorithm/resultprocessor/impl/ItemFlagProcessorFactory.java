package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.behavior.BehaviorRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum ItemFlagProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "item_flag";

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

    /**
     * 复制为完全覆盖，先清空结果已有的 flag
     */
    private ResultProcessor copyFromSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/copy_from_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            resultItem.edit(resultMeta -> {
                resultMeta.removeItemFlags(ItemFlag.values());
                sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags);
            });
        });
    }

    /**
     * 合并保留结果已有的 flag，仅追加源的 flag
     */
    private ResultProcessor mergeSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/merge_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            resultItem.edit(resultMeta -> sourceMeta.getItemFlags().forEach(resultMeta::addItemFlags));
        });
    }

    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<String> flagNames = data != null ? data.getStringList("value") : List.of();
        return ProcessorUtils.processor(COMPONENT_NAME + "/add", (sourceItem, resultItem, player) ->
            resultItem.edit(resultMeta -> {
                for (String flagName : flagNames) {
                    ItemFlag flag = parseItemFlag(flagName);
                    if (flag != null) {
                        resultMeta.addItemFlags(flag);
                    }
                }
            })
        );
    }

    /**
     * 清空结果物品的标志后写入
     */
    private ResultProcessor set(@Nullable ConfigurationSection data) {
        List<String> flagNames = data != null ? data.getStringList("value") : List.of();
        return ProcessorUtils.processor(COMPONENT_NAME + "/set", (sourceItem, resultItem, player) ->
            resultItem.edit(resultMeta -> {
                resultMeta.removeItemFlags(ItemFlag.values());
                for (String flagName : flagNames) {
                    ItemFlag flag = parseItemFlag(flagName);
                    if (flag != null) {
                        resultMeta.addItemFlags(flag);
                    }
                }
            })
        );
    }

    private ResultProcessor remove(@Nullable ConfigurationSection data) {
        List<ItemFlag> targets = parseRemoveTargets(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/remove", (sourceItem, resultItem, player) ->
            resultItem.edit(resultMeta -> {
                if (targets == null) {
                    resultMeta.removeItemFlags(ItemFlag.values());
                    return;
                }
                resultMeta.removeItemFlags(targets.toArray(ItemFlag[]::new));
            })
        );
    }

    /**
     * @return null 表示没有写 data, 即清空全部
     */
    private @Nullable List<ItemFlag> parseRemoveTargets(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            return null;
        }
        List<String> names = data.getStringList("value");
        List<ItemFlag> targets = new ArrayList<>(names.size());
        for (String name : names) {
            ItemFlag flag = parseItemFlag(name);
            if (flag != null) {
                targets.add(flag);
            }
        }
        return targets;
    }

    @Nullable
    private ItemFlag parseItemFlag(String flagName) {
        try {
            return ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            CrypticLib.warn("Unknown ItemFlag: " + flagName);
            return null;
        }
    }

}
