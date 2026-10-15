package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;

public enum SmithingTransformRecipeParser implements VanillaRecipeParser<SmithingRecipe> {

    INSTANCE;

    @Override
    public @NotNull SmithingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        return parseSafely(() -> {
            ItemStack result = requireResult(recipeConfig);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String baseId = recipeConfig.getString("base");
            RecipeChoice base = choiceParser().parse(baseId);
            String additionId = recipeConfig.getString("addition");
            RecipeChoice addition = choiceParser().parse(additionId);
            String templateId = recipeConfig.getString("template");
            RecipeChoice template = choiceParser().parse(templateId);
            return new SmithingTransformRecipe(recipeKey, result, template, base, addition);
        });
    }
}
