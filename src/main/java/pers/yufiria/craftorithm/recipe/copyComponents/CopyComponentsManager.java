package pers.yufiria.craftorithm.recipe.copyComponents;

import crypticlib.MinecraftVersion;
import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import pers.yufiria.craftorithm.recipe.copyComponents.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum CopyComponentsManager {

    INSTANCE;
    private final Map<String, CopyComponentsRule> copyNbtRules = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, CopyComponentsRules> recipeCopyNbtRules = new ConcurrentHashMap<>();

    CopyComponentsManager() {
        registerCopyNbtRule(All.INSTANCE);
        registerCopyNbtRule(Attributes.INSTANCE);
        registerCopyNbtRule(CustomModelData.INSTANCE);
        registerCopyNbtRule(DisplayName.INSTANCE);
        registerCopyNbtRule(Enchantments.INSTANCE);
        registerCopyNbtRule(ItemFlag.INSTANCE);
        registerCopyNbtRule(Lore.INSTANCE);
        registerCopyNbtRule(Unbreakable.INSTANCE);
        registerCopyNbtRule(Trim.INSTANCE);

        MinecraftVersion currentVersion = MinecraftVersion.CURRENT;
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_20_5)) {
            registerCopyNbtRule(Food.INSTANCE);
            registerCopyNbtRule(MaxStackSize.INSTANCE);
            registerCopyNbtRule(Rarity.INSTANCE);
            registerCopyNbtRule(FireResistance.INSTANCE);
            registerCopyNbtRule(HideTooltip.INSTANCE);
            registerCopyNbtRule(ItemName.INSTANCE);
        }
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_21)) {
            registerCopyNbtRule(Tool.INSTANCE);
        }
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_21_4)) {
            registerCopyNbtRule(ItemModel.INSTANCE);
            registerCopyNbtRule(CustomModelDataComponent.INSTANCE);
        }
    }

    public Optional<CopyComponentsRule> getCopyNbtRule(String name) {
        return Optional.ofNullable(copyNbtRules.get(name));
    }

    public void registerCopyNbtRule(CopyComponentsRule rule) {
        copyNbtRules.put(rule.ruleName(), rule);
    }

    public CopyComponentsRule unregisterCopyNbtRule(String ruleName) {
        return copyNbtRules.remove(ruleName);
    }

    public void addRecipeCopyNbtRules(NamespacedKey key, List<String> ruleNames) {
        List<CopyComponentsRule> rules = new ArrayList<>();
        for (String ruleName : ruleNames) {
            getCopyNbtRule(ruleName).ifPresentOrElse(
                rules::add,
                () -> {
                    IOHelper.info("&eUnknown rule: " + ruleName);
                });
        }
        recipeCopyNbtRules.put(key, new CopyComponentsRules(rules));
    }

    public boolean removeRecipeCopyNbtRules(NamespacedKey key) {
        return recipeCopyNbtRules.remove(key) != null;
    }

    public Optional<CopyComponentsRules> getRecipeCopyNbtRules(NamespacedKey key) {
        if (!recipeCopyNbtRules.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(recipeCopyNbtRules.get(key));
    }

    public void resetRecipeCopyNbtRules() {
        recipeCopyNbtRules.clear();
    }

}
