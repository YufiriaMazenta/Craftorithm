package pers.yufiria.craftorithm.hook.recipe.resultProcessor;

import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.hook.AdvancedEnchantmentsHook;
import pers.yufiria.craftorithm.recipe.resultProcessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.recipe.resultProcessor.ProcessingStrategy;
import pers.yufiria.craftorithm.recipe.resultProcessor.ResultProcessor;

import java.util.HashMap;

public class AdvancedEnchantmentsFactory implements ComponentProcessorFactory {

    public static final AdvancedEnchantmentsFactory INSTANCE = new AdvancedEnchantmentsFactory();

    @Override
    public String componentName() {
        return AdvancedEnchantmentsHook.RULE_NAME;
    }

    @Override
    public ResultProcessor createProcessor(ProcessingStrategy strategy, @Nullable ConfigurationSection data) {
        return switch (strategy) {
            case COPY_FROM_SOURCE -> new ResultProcessor() {
                @Override
                public String processorName() {
                    return AdvancedEnchantmentsHook.RULE_NAME;
                }

                @Override
                public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                    if (sourceItem == null) return;
                    HashMap<String, Integer> sourceEnchantments = AEAPI.getEnchantmentsOnItem(sourceItem);
                    if (sourceEnchantments.isEmpty()) return;
                    sourceEnchantments.forEach((key, level) -> AEAPI.applyEnchant(key, level, resultItem));
                }
            };
            case MERGE_SOURCE -> new ResultProcessor() {
                @Override
                public String processorName() {
                    return AdvancedEnchantmentsHook.RULE_NAME;
                }

                @Override
                public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                    if (sourceItem == null) return;
                    HashMap<String, Integer> sourceEnchantments = AEAPI.getEnchantmentsOnItem(sourceItem);
                    if (sourceEnchantments.isEmpty()) return;
                    HashMap<String, Integer> resultEnchantments = AEAPI.getEnchantmentsOnItem(resultItem);
                    sourceEnchantments.forEach((key, level) -> {
                        Integer resultLevel = resultEnchantments.get(key);
                        if (resultLevel == null || level > resultLevel) {
                            AEAPI.applyEnchant(key, level, resultItem);
                        }
                    });
                }
            };
            case REMOVE -> new ResultProcessor() {
                @Override
                public String processorName() {
                    return AdvancedEnchantmentsHook.RULE_NAME;
                }

                @Override
                public void processItem(@Nullable ItemStack sourceItem, @NotNull ItemStack resultItem, @Nullable Player player) {
                    // AE API 没有 removeEnchant 方法，使用 level=0 来移除
                    HashMap<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(resultItem);
                    enchants.keySet().forEach(key -> AEAPI.applyEnchant(key, 0, resultItem));
                }
            };
            default -> throw new UnsupportedOperationException("ae_enchantments does not support " + strategy);
        };
    }

}
