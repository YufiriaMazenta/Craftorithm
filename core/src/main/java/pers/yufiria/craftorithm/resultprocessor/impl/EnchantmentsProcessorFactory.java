package pers.yufiria.craftorithm.resultprocessor.impl;

import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.behavior.ComponentRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProvider;
import pers.yufiria.craftorithm.resultprocessor.value.ValueProviders;

import java.util.ArrayList;
import java.util.List;

public enum EnchantmentsProcessorFactory implements ComponentProcessorFactory {

    INSTANCE;

    public static final String COMPONENT_NAME = "enchantments";

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public void declareBehaviors(ComponentRegistrar registrar) {
        registrar.behavior(Behaviors.COPY_FROM_SOURCE, (context, data) -> copyFromSource())
            .behavior(Behaviors.ADD, (context, data) -> add(data))
            .behavior(Behaviors.SET, (context, data) -> set(data))
            .behavior(Behaviors.MERGE_SOURCE, (context, data) -> mergeSource())
            .behavior(Behaviors.REMOVE, (context, data) -> remove(data));
    }

    /**
     * 复制时源等级不低于结果等级就覆盖（结果等级更高才保留）
     */
    private ResultProcessor copyFromSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/copy_from_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            if (!sourceMeta.hasEnchants()) {
                return;
            }
            resultItem.edit(resultMeta -> sourceMeta.getEnchants().forEach((enchant, level) -> {
                if (resultMeta.hasEnchant(enchant)) {
                    if (resultMeta.getEnchantLevel(enchant) > level) {
                        return;
                    }
                    resultMeta.removeEnchant(enchant);
                }
                resultMeta.addEnchant(enchant, level, true);
            }));
        });
    }

    /**
     * 合并时仅源等级严格更高才覆盖（等级相同保留结果）
     */
    private ResultProcessor mergeSource() {
        return ProcessorUtils.processorRequireSource(COMPONENT_NAME + "/merge_source", (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.meta();
            if (!sourceMeta.hasEnchants()) {
                return;
            }
            resultItem.edit(resultMeta -> sourceMeta.getEnchants().forEach((enchant, level) -> {
                if (resultMeta.hasEnchant(enchant)) {
                    if (resultMeta.getEnchantLevel(enchant) >= level) {
                        return;
                    }
                    resultMeta.removeEnchant(enchant);
                }
                resultMeta.addEnchant(enchant, level, true);
            }));
        });
    }

    private ResultProcessor add(@Nullable ConfigurationSection data) {
        List<EnchantmentEntry> enchants = parseEnchantments(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/add", (sourceItem, resultItem, player) -> {
            resultItem.edit(resultMeta -> {
                for (EnchantmentEntry entry : enchants) {
                    Integer level = entry.level().resolve(player, sourceItem, resultItem);
                    if (level == null) {
                        //脚本求值失败: 只跳过这一条附魔, 其余照常写入
                        continue;
                    }
                    resultMeta.addEnchant(entry.enchantment(), level, true);
                }
            });
        });
    }

    /**
     * 清空结果物品的全部附魔后写入
     */
    private ResultProcessor set(@Nullable ConfigurationSection data) {
        List<EnchantmentEntry> enchants = parseEnchantments(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/set", (sourceItem, resultItem, player) -> {
            resultItem.edit(resultMeta -> {
                for (Enchantment enchantment : new ArrayList<>(resultMeta.getEnchants().keySet())) {
                    resultMeta.removeEnchant(enchantment);
                }
                for (EnchantmentEntry entry : enchants) {
                    Integer level = entry.level().resolve(player, sourceItem, resultItem);
                    if (level == null) {
                        continue;
                    }
                    resultMeta.addEnchant(entry.enchantment(), level, true);
                }
            });
        });
    }

    private ResultProcessor remove(@Nullable ConfigurationSection data) {
        List<Enchantment> targets = parseRemoveTargets(data);
        return ProcessorUtils.processor(COMPONENT_NAME + "/remove", (sourceItem, resultItem, player) -> {
            resultItem.edit(resultMeta -> {
                if (targets == null) {
                    for (Enchantment enchantment : new ArrayList<>(resultMeta.getEnchants().keySet())) {
                        resultMeta.removeEnchant(enchantment);
                    }
                    return;
                }
                for (Enchantment enchantment : targets) {
                    if (resultMeta.hasEnchant(enchantment)) {
                        resultMeta.removeEnchant(enchantment);
                    }
                }
            });
        });
    }

    /**
     * @return null 表示没有写 data, 即清空全部
     */
    private @Nullable List<Enchantment> parseRemoveTargets(@Nullable ConfigurationSection data) {
        if (data == null || data.getKeys(false).isEmpty()) {
            return null;
        }
        List<String> names = data.getStringList("value");
        if (names.isEmpty()) {
            names = List.copyOf(data.getKeys(false));
        }
        List<Enchantment> targets = new ArrayList<>(names.size());
        for (String name : names) {
            Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.fromString(name));
            if (enchantment == null) {
                CrypticLib.warn(COMPONENT_NAME + "/remove has unknown enchantment '" + name + "', skipped");
                continue;
            }
            targets.add(enchantment);
        }
        return targets;
    }

    private record EnchantmentEntry(Enchantment enchantment, ValueProvider<Integer> level) {
    }

    private List<EnchantmentEntry> parseEnchantments(@Nullable ConfigurationSection data) {
        if (data == null) return List.of();
        List<EnchantmentEntry> result = new ArrayList<>();
        for (String key : data.getKeys(false)) {
            Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.fromString(key));
            if (ench != null) {
                ValueProvider<Integer> level = ValueProviders.integer(data, key, 0, COMPONENT_NAME + "/" + key);
                result.add(new EnchantmentEntry(ench, level));
            }
        }
        return result;
    }

}
