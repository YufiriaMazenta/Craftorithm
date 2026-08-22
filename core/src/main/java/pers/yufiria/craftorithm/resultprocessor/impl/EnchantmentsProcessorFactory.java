package pers.yufiria.craftorithm.resultprocessor.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
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

public class EnchantmentsProcessorFactory implements ComponentProcessorFactory {

    public static final EnchantmentsProcessorFactory INSTANCE = new EnchantmentsProcessorFactory();

    public static final String COMPONENT_NAME = "enchantments";

    private static final Map<String, Function<ConfigurationSection, ResultProcessor>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("copy_from_source", data -> copyFromSource());
        HANDLERS.put("add", EnchantmentsProcessorFactory::add);
        HANDLERS.put("merge_source", data -> mergeSource());
        HANDLERS.put("remove", EnchantmentsProcessorFactory::remove);
    }

    @Override
    public String componentName() {
        return COMPONENT_NAME;
    }

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = HANDLERS.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(COMPONENT_NAME + " does not support type: " + type);
        }
        return handler.apply(data);
    }

    /**
     * 复制时源等级不低于结果等级就覆盖（结果等级更高才保留）
     */
    private static ResultProcessor copyFromSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasEnchants()) {
                sourceMeta.getEnchants().forEach((enchant, level) -> {
                    if (resultMeta.hasEnchant(enchant)) {
                        if (resultMeta.getEnchantLevel(enchant) > level) {
                            return;
                        }
                        resultMeta.removeEnchant(enchant);
                    }
                    resultMeta.addEnchant(enchant, level, true);
                });
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    /**
     * 合并时仅源等级严格更高才覆盖（等级相同保留结果）
     */
    private static ResultProcessor mergeSource() {
        return processorRequireSource(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta sourceMeta = sourceItem.getItemMeta();
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (sourceMeta.hasEnchants()) {
                sourceMeta.getEnchants().forEach((enchant, level) -> {
                    if (resultMeta.hasEnchant(enchant)) {
                        if (resultMeta.getEnchantLevel(enchant) >= level) {
                            return;
                        }
                        resultMeta.removeEnchant(enchant);
                    }
                    resultMeta.addEnchant(enchant, level, true);
                });
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor add(@Nullable ConfigurationSection data) {
        List<EnchantmentEntry> enchants = parseEnchantments(data);
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            enchants.forEach(e -> resultMeta.addEnchant(e.enchantment(), e.level(), true));
            resultItem.setItemMeta(resultMeta);
        });
    }

    private static ResultProcessor remove(@Nullable ConfigurationSection data) {
        return processor(COMPONENT_NAME, (sourceItem, resultItem, player) -> {
            ItemMeta resultMeta = resultItem.getItemMeta();
            if (data == null || data.getKeys(false).isEmpty()) {
                for (Enchantment ench : new ArrayList<>(resultMeta.getEnchants().keySet())) {
                    resultMeta.removeEnchant(ench);
                }
            } else {
                List<String> removeList = data.getStringList("value");
                if (removeList.isEmpty()) {
                    removeList = List.copyOf(data.getKeys(false));
                }
                for (String enchName : removeList) {
                    Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.fromString(enchName));
                    if (ench != null && resultMeta.hasEnchant(ench)) {
                        resultMeta.removeEnchant(ench);
                    }
                }
            }
            resultItem.setItemMeta(resultMeta);
        });
    }

    private record EnchantmentEntry(Enchantment enchantment, int level) {}

    private static List<EnchantmentEntry> parseEnchantments(@Nullable ConfigurationSection data) {
        if (data == null) return List.of();
        List<EnchantmentEntry> result = new ArrayList<>();
        for (String key : data.getKeys(false)) {
            Enchantment ench = Registry.ENCHANTMENT.get(NamespacedKey.fromString(key));
            if (ench != null) {
                int level = data.getInt(key);
                result.add(new EnchantmentEntry(ench, level));
            }
        }
        return result;
    }

}
