package pers.yufiria.craftorithm.recipe.keepNbt;

import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import pers.yufiria.craftorithm.recipe.keepNbt.impl.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum CopyNbtManager {

    INSTANCE;
    private final Map<String, CopyNbtRule> copyNbtRules = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, CopyNbtRules> recipeCopyNbtRules = new ConcurrentHashMap<>();

    CopyNbtManager() {
        registerCopyNbtRule(All.INSTANCE);
        registerCopyNbtRule(Attributes.INSTANCE);
        registerCopyNbtRule(CustomModelData.INSTANCE);
        registerCopyNbtRule(DisplayName.INSTANCE);
        registerCopyNbtRule(Enchantments.INSTANCE);
        registerCopyNbtRule(Food.INSTANCE);
        registerCopyNbtRule(ItemFlag.INSTANCE);
        registerCopyNbtRule(Lore.INSTANCE);
        registerCopyNbtRule(MaxStackSize.INSTANCE);
        registerCopyNbtRule(Rarity.INSTANCE);
    }

    public Optional<CopyNbtRule> getCopyNbtRule(String name) {
        return Optional.ofNullable(copyNbtRules.get(name));
    }

    public void registerCopyNbtRule(CopyNbtRule rule) {
        copyNbtRules.put(rule.ruleName(), rule);
    }

    public CopyNbtRule unregisterCopyNbtRule(String ruleName) {
        return copyNbtRules.remove(ruleName);
    }

    public void addRecipeCopyNbtRules(NamespacedKey key, List<String> ruleNames) {
        List<CopyNbtRule> rules = new ArrayList<>();
        for (String ruleName : ruleNames) {
            getCopyNbtRule(ruleName).ifPresentOrElse(
                rules::add,
                () -> {
                    IOHelper.info("&eUnknown rule: " + ruleName);
                });
        }
        recipeCopyNbtRules.put(key, new CopyNbtRules(rules));
    }

    public boolean removeRecipeCopyNbtRules(NamespacedKey key) {
        return recipeCopyNbtRules.remove(key) != null;
    }

    public Optional<CopyNbtRules> getRecipeCopyNbtRules(NamespacedKey key) {
        if (!recipeCopyNbtRules.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(recipeCopyNbtRules.get(key));
    }

    public void resetRecipeCopyNbtRules() {
        recipeCopyNbtRules.clear();
    }

}
