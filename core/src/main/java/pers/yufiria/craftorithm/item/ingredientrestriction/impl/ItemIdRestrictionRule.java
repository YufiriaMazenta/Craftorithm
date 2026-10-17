package pers.yufiria.craftorithm.item.ingredientrestriction.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ingredientrestriction.IngredientRestrictionRule;
import pers.yufiria.craftorithm.item.ingredientrestriction.IngredientRestrictionRuleFactory;
import pers.yufiria.craftorithm.item.ingredientrestriction.RecipeKeyMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 基于物品 ID 的合成限制规则
 * <p>
 * 匹配指定物品 ID 的物品，阻止其参与指定配方
 */
public final class ItemIdRestrictionRule implements IngredientRestrictionRule {

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
        this.itemIds = List.of(Objects.requireNonNull(NamespacedItemId.fromString(itemId)));
        this.recipeMatchers = recipeMatchers;
    }

    @Override
    public boolean isBlocked(@NotNull ItemStack item, @NotNull NamespacedKey recipeKey) {
        if (!ItemManager.INSTANCE.containsItem(itemIds, item)) {
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

    public Collection<NamespacedItemId> itemIds() {
        return itemIds;
    }

    public List<RecipeKeyMatcher> recipeMatchers() {
        return recipeMatchers;
    }
}
