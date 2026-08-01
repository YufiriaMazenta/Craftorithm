package pers.yufiria.craftorithm.hook.recipe.copyComponents;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.hook.AdvancedEnchantmentsHook;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsRule;

import java.util.HashMap;

@LifeCycleTaskSettings(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum AdvancedEnchantmentsEnchantments implements CopyComponentsRule {

    INSTANCE;

    @Override
    public String ruleName() {
        return AdvancedEnchantmentsHook.RULE_NAME;
    }

    @Override
    public @NotNull ItemMeta processItemMeta(@NotNull ItemMeta baseMeta, @NotNull ItemMeta resultMeta) {
        ItemStack tmpItem = new ItemStack(Material.DIAMOND_SWORD, 1);
        tmpItem.setItemMeta(baseMeta);
        HashMap<String, Integer> baseEnchantments = AEAPI.getEnchantmentsOnItem(tmpItem);
        if (baseEnchantments.isEmpty()) {
            return resultMeta;
        }
        ItemStack tmpResult = new ItemStack(Material.DIAMOND_SWORD, 1);
        baseEnchantments.forEach((key, level) -> {
            AEAPI.applyEnchant(key, level, tmpResult);
        });
        return tmpResult.getItemMeta();
    }
}
