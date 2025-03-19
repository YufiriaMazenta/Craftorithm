package pers.yufiria.craftorithm.recipe.keepNbt;

import crypticlib.util.IOHelper;
import org.bukkit.NamespacedKey;
import pers.yufiria.craftorithm.recipe.keepNbt.impl.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum KeepNbtManager {

    INSTANCE;
    private final Map<String, KeepNbtRule> keepNbtRules = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, KeepNbtRules> recipeKeepNbtRules = new ConcurrentHashMap<>();

    KeepNbtManager() {
        registerKeepNbtRule(All.INSTANCE);
        registerKeepNbtRule(Attributes.INSTANCE);
        registerKeepNbtRule(CustomModelData.INSTANCE);
        registerKeepNbtRule(DisplayName.INSTANCE);
        registerKeepNbtRule(Enchantments.INSTANCE);
        registerKeepNbtRule(Food.INSTANCE);
        registerKeepNbtRule(ItemFlag.INSTANCE);
        registerKeepNbtRule(Lore.INSTANCE);
        registerKeepNbtRule(MaxStackSize.INSTANCE);
        registerKeepNbtRule(Rarity.INSTANCE);
    }

    public Optional<KeepNbtRule> getKeepNbtRule(String name) {
        return Optional.ofNullable(keepNbtRules.get(name));
    }

    public void registerKeepNbtRule(KeepNbtRule rule) {
        keepNbtRules.put(rule.ruleName(), rule);
    }

    public KeepNbtRule unregisterKeepNbtRule(String ruleName) {
        return keepNbtRules.remove(ruleName);
    }

    public void addRecipeKeepNbtRules(NamespacedKey key, List<String> ruleNames) {
        List<KeepNbtRule> rules = new ArrayList<>();
        for (String ruleName : ruleNames) {
            getKeepNbtRule(ruleName).ifPresentOrElse(
                rules::add,
                () -> {
                    IOHelper.info("&eUnknown rule: " + ruleName);
                });
        }
        recipeKeepNbtRules.put(key, new KeepNbtRules(rules));
    }

    public boolean removeRecipeKeepNbtRules(NamespacedKey key) {
        return recipeKeepNbtRules.remove(key) != null;
    }

    public Optional<KeepNbtRules> getRecipeKeepNbtRules(NamespacedKey key) {
        if (!recipeKeepNbtRules.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.ofNullable(recipeKeepNbtRules.get(key));
    }

    public void resetRecipeKeepNbtRules() {
        recipeKeepNbtRules.clear();
    }

}
