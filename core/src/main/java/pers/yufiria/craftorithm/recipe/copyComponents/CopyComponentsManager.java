package pers.yufiria.craftorithm.recipe.copyComponents;

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
        for (SimpleCopyComponentsRules rule : SimpleCopyComponentsRules.values()) {
            if (rule.supportedByCurrentVersion()) {
                registerCopyNbtRuleCreator(rule.ruleName(), arg -> rule);
            }
        }
        registerCopyNbtRuleCreator(CustomPersistentData.RULE_NAME, CustomPersistentData::new);
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
        if (ruleCreator == null) {
            return Optional.empty();
        }
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
     * @param recipeKey 配方的key
     * @param ruleStrList 所需的组件保留策略
     */
    public void addRecipeCopyNbtRules(NamespacedKey recipeKey, List<String> ruleStrList) {
        List<CopyComponentsRule> rules = new ArrayList<>();
        for (String ruleName : ruleStrList) {
            compileRule(ruleName).ifPresentOrElse(
                rules::add,
                () -> {
                    IOHelper.info("&eUnknown rule: " + ruleName);
                });
        }
        recipeCopyNbtRules.put(recipeKey, new CopyComponentsRules(rules));
    }

    public boolean removeRecipeCopyNbtRules(NamespacedKey recipeKey) {
        return recipeCopyNbtRules.remove(recipeKey) != null;
    }

    public Optional<CopyComponentsRules> getRecipeCopyNbtRules(NamespacedKey recipeKey) {
        if (!recipeCopyNbtRules.containsKey(recipeKey)) {
            return Optional.empty();
        }
        return Optional.ofNullable(recipeCopyNbtRules.get(recipeKey));
    }

    public void resetRecipeCopyNbtRules() {
        recipeCopyNbtRules.clear();
    }

}
