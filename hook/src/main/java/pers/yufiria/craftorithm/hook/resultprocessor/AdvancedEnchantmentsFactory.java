package pers.yufiria.craftorithm.hook.resultprocessor;

import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.hook.AdvancedEnchantmentsHook;
import pers.yufiria.craftorithm.resultprocessor.ItemWrapper;
import pers.yufiria.craftorithm.resultprocessor.ComponentProcessorFactory;
import pers.yufiria.craftorithm.resultprocessor.behavior.ComponentRegistrar;
import pers.yufiria.craftorithm.resultprocessor.behavior.Behaviors;
import pers.yufiria.craftorithm.resultprocessor.ResultProcessor;

import java.util.HashMap;

public enum AdvancedEnchantmentsFactory implements ComponentProcessorFactory {

    INSTANCE;

    @Override
    public String componentName() {
        return AdvancedEnchantmentsHook.RULE_NAME;
    }

    @Override
    public void declareBehaviors(ComponentRegistrar registrar) {
        registrar.behavior(Behaviors.COPY_FROM_SOURCE, (context, data) -> copyFromSource())
            .behavior(Behaviors.MERGE_SOURCE, (context, data) -> mergeSource())
            .behavior(Behaviors.REMOVE, (context, data) -> remove());
    }

    private ResultProcessor copyFromSource() {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return AdvancedEnchantmentsHook.RULE_NAME + "/copy_from_source";
            }

            @Override
            public void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player) {
                if (sourceItem == null) return;
                HashMap<String, Integer> sourceEnchantments = AEAPI.getEnchantmentsOnItem(sourceItem.snapshot());
                if (sourceEnchantments.isEmpty()) return;
                ItemStack working = resultItem.snapshot();
                sourceEnchantments.forEach((key, level) -> AEAPI.applyEnchant(key, level, working));
                resultItem.setMeta(working.getItemMeta());
            }
        };
    }

    private ResultProcessor mergeSource() {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return AdvancedEnchantmentsHook.RULE_NAME + "/merge_source";
            }

            @Override
            public void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player) {
                if (sourceItem == null) return;
                HashMap<String, Integer> sourceEnchantments = AEAPI.getEnchantmentsOnItem(sourceItem.snapshot());
                if (sourceEnchantments.isEmpty()) return;
                ItemStack working = resultItem.snapshot();
                HashMap<String, Integer> resultEnchantments = AEAPI.getEnchantmentsOnItem(working);
                sourceEnchantments.forEach((key, level) -> {
                    Integer resultLevel = resultEnchantments.get(key);
                    if (resultLevel == null || level > resultLevel) {
                        AEAPI.applyEnchant(key, level, working);
                    }
                });
                resultItem.setMeta(working.getItemMeta());
            }
        };
    }

    private ResultProcessor remove() {
        return new ResultProcessor() {
            @Override
            public String processorName() {
                return AdvancedEnchantmentsHook.RULE_NAME + "/remove";
            }

            @Override
            public void processItem(@Nullable ItemWrapper sourceItem, @NotNull ItemWrapper resultItem, @Nullable Player player) {
                // AE API 没有 removeEnchant 方法，使用 level=0 来移除
                ItemStack working = resultItem.snapshot();
                HashMap<String, Integer> enchants = AEAPI.getEnchantmentsOnItem(working);
                enchants.keySet().forEach(key -> AEAPI.applyEnchant(key, 0, working));
                resultItem.setMeta(working.getItemMeta());
            }
        };
    }

}
