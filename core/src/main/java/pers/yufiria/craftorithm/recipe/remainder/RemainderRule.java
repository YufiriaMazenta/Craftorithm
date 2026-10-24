package pers.yufiria.craftorithm.recipe.remainder;

import crypticlib.CrypticLib;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.ingredient.ItemIdIngredientMatcher;
import pers.yufiria.craftorithm.recipe.ingredient.ItemIdRecipeChoiceParser;

/**
 * 一条材料剩余物规则, 同一物品只应用第一条匹配规则
 */
public record RemainderRule(
    @NotNull String ingredientId,
    @NotNull ItemIdIngredientMatcher matcher,
    @NotNull RemainderProcessor processor
) {

    /**
     * 从单条配置构造规则
     *
     * @return 配置无效时返回null
     */
    public static @Nullable RemainderRule fromConfig(
        @NotNull ConfigurationSection section,
        @NotNull NamespacedKey recipeKey
    ) {
        String ingredientId = section.getString("ingredient");
        if (ingredientId == null || ingredientId.isBlank()) {
            CrypticLib.warn("Missing or invalid remainder ingredient in recipe: " + recipeKey);
            return null;
        }

        ItemIdIngredientMatcher matcher;
        try {
            RecipeChoice recipeChoice = ItemIdRecipeChoiceParser.INSTANCE.parse(ingredientId);
            if (!(recipeChoice instanceof ItemIdIngredientMatcher itemIdMatcher)) {
                CrypticLib.warn("Remainder ingredient does not support item IDs: " + ingredientId + " in recipe: " + recipeKey);
                return null;
            }
            matcher = itemIdMatcher;
        } catch (Throwable throwable) {
            CrypticLib.warn(
                "Cannot parse remainder ingredient " + ingredientId + " in recipe: " + recipeKey + ": " + throwable.getMessage()
            );
            return null;
        }

        RemainderProcessor processor = RemainderProcessorRegistry.INSTANCE.create(section, recipeKey);
        if (processor == null) {
            return null;
        }
        return new RemainderRule(ingredientId, matcher, processor);
    }

    public boolean matches(@NotNull ItemStack itemStack) {
        return matcher.matches(itemStack);
    }

}
