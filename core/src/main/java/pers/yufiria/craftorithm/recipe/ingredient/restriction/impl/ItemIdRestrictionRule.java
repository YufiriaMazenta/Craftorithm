package pers.yufiria.craftorithm.recipe.ingredient.restriction.impl;

import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.recipe.ingredient.restriction.IngredientRestrictionRule;
import pers.yufiria.craftorithm.recipe.ingredient.restriction.IngredientRestrictionRuleFactory;
import pers.yufiria.craftorithm.recipe.ingredient.restriction.RecipeKeyMatcher;
import pers.yufiria.craftorithm.recipe.ingredient.ItemIdIngredientMatcher;

import java.util.Collection;
import java.util.List;

/**
 * 基于物品 ID 的合成限制规则
 * <p>
 * 匹配指定物品 ID 的物品，阻止其参与指定配方
 */
public final class ItemIdRestrictionRule implements IngredientRestrictionRule, ItemIdIngredientMatcher {

    public static final IngredientRestrictionRuleFactory FACTORY = new IngredientRestrictionRuleFactory() {
        @Override
        public @NotNull String type() {
            return "item_id";
        }
        @Override
        public @NotNull IngredientRestrictionRule load(@NotNull ConfigurationSection section) {
            String itemId = section.getString("item_id", "");
            List<String> recipes = section.getStringList("recipes");
            return new ItemIdRestrictionRule(itemId, RecipeKeyMatcher.ofList(recipes));
        }
    };

    private final Collection<NamespacedItemId> itemIds;
    private final List<RecipeKeyMatcher> recipeMatchers;

    public ItemIdRestrictionRule(String itemId, List<RecipeKeyMatcher> recipeMatchers) {
        NamespacedItemId namespacedItemId = NamespacedItemId.fromString(itemId);
        if (namespacedItemId == null) {
            //配置里的物品id无法解析时不能让整次配置重载失败, 记录一条警告并让该条规则永不命中
            CrypticLib.warn("Unknown item id '" + itemId + "' in config.yml ingredient_restriction_rules, this rule will be ignored");
            this.itemIds = List.of();
        } else {
            this.itemIds = List.of(namespacedItemId);
        }
        this.recipeMatchers = recipeMatchers;
    }

    @Override
    public boolean isBlocked(@NotNull ItemStack item, @NotNull NamespacedKey recipeKey) {
        if (itemIds.isEmpty()) {
            return false;
        }
        if (!ItemIdIngredientMatcher.super.matches(item)) {
            return false;
        }
        String recipeKeyStr = recipeKey.toString();
        for (RecipeKeyMatcher matcher : recipeMatchers) {
            if (matcher.matches(recipeKeyStr)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<NamespacedItemId> itemIds() {
        return itemIds;
    }

    public List<RecipeKeyMatcher> recipeMatchers() {
        return recipeMatchers;
    }
}
