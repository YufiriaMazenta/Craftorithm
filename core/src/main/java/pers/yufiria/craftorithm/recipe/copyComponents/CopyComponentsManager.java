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
import java.util.function.Function;

public enum CopyComponentsManager {

    INSTANCE;
    private final Map<String, Function<String, CopyComponentsRule>> copyNbtRuleCreatorMap = new ConcurrentHashMap<>();
    private final Map<NamespacedKey, CopyComponentsRules> recipeCopyNbtRules = new ConcurrentHashMap<>();

    CopyComponentsManager() {
        registerCopyNbtRuleCreator(All.INSTANCE.ruleName(), arg -> All.INSTANCE);
        registerCopyNbtRuleCreator(Attributes.INSTANCE.ruleName(), arg -> Attributes.INSTANCE);
        registerCopyNbtRuleCreator(CustomModelData.INSTANCE.ruleName(), arg -> CustomModelData.INSTANCE);
        registerCopyNbtRuleCreator(DisplayName.INSTANCE.ruleName(), arg -> DisplayName.INSTANCE);
        registerCopyNbtRuleCreator(Enchantments.INSTANCE.ruleName(), arg -> Enchantments.INSTANCE);
        registerCopyNbtRuleCreator(ItemFlag.INSTANCE.ruleName(), arg -> ItemFlag.INSTANCE);
        registerCopyNbtRuleCreator(Lore.INSTANCE.ruleName(), arg -> Lore.INSTANCE);
        registerCopyNbtRuleCreator(Unbreakable.INSTANCE.ruleName(), arg -> Unbreakable.INSTANCE);
        registerCopyNbtRuleCreator(Trim.INSTANCE.ruleName(), arg -> Trim.INSTANCE);
        registerCopyNbtRuleCreator(CustomPersistentData.RULE_NAME, CustomPersistentData::new);

        MinecraftVersion currentVersion = MinecraftVersion.CURRENT;
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_20_5)) {
            registerCopyNbtRuleCreator(Food.INSTANCE.ruleName(), arg -> Food.INSTANCE);
            registerCopyNbtRuleCreator(MaxStackSize.INSTANCE.ruleName(), arg -> MaxStackSize.INSTANCE);
            registerCopyNbtRuleCreator(Rarity.INSTANCE.ruleName(), arg -> Rarity.INSTANCE);
            registerCopyNbtRuleCreator(FireResistance.INSTANCE.ruleName(), arg -> FireResistance.INSTANCE);
            registerCopyNbtRuleCreator(HideTooltip.INSTANCE.ruleName(), arg -> HideTooltip.INSTANCE);
            registerCopyNbtRuleCreator(ItemName.INSTANCE.ruleName(), arg -> ItemName.INSTANCE);
        }
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_21)) {
            registerCopyNbtRuleCreator(Tool.INSTANCE.ruleName(), arg -> Tool.INSTANCE);
        }
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_21_4)) {
            registerCopyNbtRuleCreator(ItemModel.INSTANCE.ruleName(), arg -> ItemModel.INSTANCE);
            registerCopyNbtRuleCreator(CustomModelDataComponent.INSTANCE.ruleName(), arg -> CustomModelDataComponent.INSTANCE);
        }
    }

    public Optional<CopyComponentsRule> compileRule(String ruleStr) {
        String ruleName, arg;
        int spaceIndex = ruleStr.indexOf(' ');
        if (spaceIndex == -1) {
            ruleName = ruleStr;
            arg = null;
        } else {
            ruleName = ruleStr.substring(0, spaceIndex);
            arg = ruleStr.substring(spaceIndex + 1);
        }
        Function<String, CopyComponentsRule> ruleCreator = copyNbtRuleCreatorMap.get(ruleName);
        return Optional.ofNullable(ruleCreator.apply(arg));
    }

    public void registerCopyNbtRuleCreator(String ruleName,  Function<String, CopyComponentsRule> ruleCreator) {
        copyNbtRuleCreatorMap.put(ruleName, ruleCreator);
    }

    public Function<String, CopyComponentsRule> unregisterCopyNbtRule(String ruleName) {
        return copyNbtRuleCreatorMap.remove(ruleName);
    }

    /**
     * 添加对于某个配方的组件保留策略
     * @param key 配方的key
     * @param ruleStrList 所需的组件保留策略
     */
    public void addRecipeCopyNbtRules(NamespacedKey key, List<String> ruleStrList) {
        List<CopyComponentsRule> rules = new ArrayList<>();
        for (String ruleName : ruleStrList) {
            compileRule(ruleName).ifPresentOrElse(
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
