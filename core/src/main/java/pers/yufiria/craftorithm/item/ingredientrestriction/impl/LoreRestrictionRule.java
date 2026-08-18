package pers.yufiria.craftorithm.item.ingredientrestriction.impl;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ingredientrestriction.IngredientRestrictionRule;
import pers.yufiria.craftorithm.item.ingredientrestriction.IngredientRestrictionRuleFactory;
import pers.yufiria.craftorithm.item.ingredientrestriction.RecipeKeyMatcher;

import java.util.List;

/**
 * 基于 Lore 的合成限制规则
 * <p>
 * 匹配物品 Lore 中包含指定文本（去除颜色代码后）的物品，阻止其参与指定配方
 */
public final class LoreRestrictionRule implements IngredientRestrictionRule {

    public static final IngredientRestrictionRuleFactory FACTORY = new IngredientRestrictionRuleFactory() {
        @Override
        public @NotNull String type() {
            return "lore";
        }
        @Override
        public @NotNull IngredientRestrictionRule load(@NotNull ConfigurationSection section) {
            String lore = section.getString("lore", "");
            List<String> recipes = section.getStringList("recipes");
            return new LoreRestrictionRule(lore, RecipeKeyMatcher.ofList(recipes));
        }
    };

    private final String lore;
    private final List<RecipeKeyMatcher> matchers;

    public LoreRestrictionRule(String lore, List<RecipeKeyMatcher> matchers) {
        this.lore = lore;
        this.matchers = matchers;
    }

    @Override
    public boolean isBlocked(@NotNull ItemStack item, @NotNull NamespacedKey recipeKey) {
        if (!item.hasItemMeta() || item.getItemMeta().getLore() == null) {
            return false;
        }
        String recipeKeyStr = recipeKey.toString();
        for (String loreLine : item.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(loreLine);
            if (!lore.equals(stripped)) {
                continue;
            }
            for (RecipeKeyMatcher matcher : matchers) {
                if (matcher.matches(recipeKeyStr)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String lore() {
        return lore;
    }

    public List<RecipeKeyMatcher> matchers() {
        return matchers;
    }
}
