package pers.yufiria.craftorithm.recipe.blockrule.impl;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.blockrule.BlockCraftRule;
import pers.yufiria.craftorithm.recipe.blockrule.BlockCraftRuleFactory;
import pers.yufiria.craftorithm.recipe.blockrule.RecipeKeyMatcher;

import java.util.List;

/**
 * 基于物品 ID 的合成限制规则
 * <p>
 * 匹配指定物品 ID 的物品，阻止其参与指定配方
 */
public final class ItemIdBlockCraftRule implements BlockCraftRule {

    public static final BlockCraftRuleFactory FACTORY = section -> {
        String itemId = section.getString("item_id", "");
        List<String> recipes = section.getStringList("recipes");
        return new ItemIdBlockCraftRule(itemId, RecipeKeyMatcher.ofList(recipes));
    };

    private final String itemId;
    private final List<RecipeKeyMatcher> recipeMatchers;

    public ItemIdBlockCraftRule(String itemId, List<RecipeKeyMatcher> recipeMatchers) {
        this.itemId = itemId;
        this.recipeMatchers = recipeMatchers;
    }

    @Override
    public @NotNull String type() {
        return "item_id";
    }

    @Override
    public boolean isBlocked(@NotNull ItemStack item, @NotNull NamespacedKey recipeKey) {
        NamespacedItemIdStack itemIdStack = ItemManager.INSTANCE.matchItemIdOrVanilla(item, true).orElse(null);
        if (itemIdStack == null) {
            return false;
        }
        if (!itemId.equals(itemIdStack.itemId().toString())) {
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

    public String itemId() {
        return itemId;
    }

    public List<RecipeKeyMatcher> recipeMatchers() {
        return recipeMatchers;
    }
}
