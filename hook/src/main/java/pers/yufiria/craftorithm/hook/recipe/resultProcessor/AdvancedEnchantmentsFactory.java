package pers.yufiria.craftorithm.hook.recipe.resultProcessor;

import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.hook.AdvancedEnchantmentsHook;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AdvancedEnchantmentsFactory implements ComponentProcessorFactory {

    public static final AdvancedEnchantmentsFactory INSTANCE = new AdvancedEnchantmentsFactory();

    private static final Map<String, Function<ConfigurationSection, ResultProcessor>> HANDLERS = new HashMap<>();

    static {
        HANDLERS.put("copy_from_source", data -> copyFromSource());
        HANDLERS.put("merge_source", data -> mergeSource());
        HANDLERS.put("remove", data -> remove());
    }

    @Override
    public String componentName() {
        return AdvancedEnchantmentsHook.RULE_NAME;
    }

    @Override
    public ResultProcessor createProcessor(String type, @Nullable ConfigurationSection data) {
        Function<ConfigurationSection, ResultProcessor> handler = HANDLERS.get(type);
        if (handler == null) {
            throw new UnsupportedOperationException(AdvancedEnchantmentsHook.RULE_NAME + " does not support type: " + type);
        }
        return handler.apply(data);
    }

    private static ResultProcessor copyFromSource() {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return AdvancedEnchantmentsHook.RULE_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                if (sourceItem == null) return;
                java.util.HashMap<String, Integer> sourceEnchantments = AEAPI.getEnchantmentsOnItem(sourceItem);
                if (sourceEnchantments.isEmpty()) return;
                sourceEnchantments.forEach((key, level) -> AEAPI.applyEnchant(key, level, resultItem));
            }
        };
    }

    private static ResultProcessor mergeSource() {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return AdvancedEnchantmentsHook.RULE_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                if (sourceItem == null) return;
                java.util.HashMap<String, Integer> sourceEnchantments = AEAPI.getEnchantmentsOnItem(sourceItem);
                if (sourceEnchantments.isEmpty()) return;
                java.util.HashMap<String, Integer> resultEnchantments = AEAPI.getEnchantmentsOnItem(resultItem);
                sourceEnchantments.forEach((key, level) -> {
                    Integer resultLevel = resultEnchantments.get(key);
                    if (resultLevel == null || level > resultLevel) {
                        AEAPI.applyEnchant(key, level, resultItem);
                    }
                });
            }
        };
    }

    private static ResultProcessor remove() {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return AdvancedEnchantmentsHook.RULE_NAME;
            }

            @Override
            public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                // AE API 没有 removeEnchant 方法，使用 level=0 来移除
                java.util.HashMap<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(resultItem);
                enchants.keySet().forEach(key -> AEAPI.applyEnchant(key, 0, resultItem));
            }
        };
    }

}
