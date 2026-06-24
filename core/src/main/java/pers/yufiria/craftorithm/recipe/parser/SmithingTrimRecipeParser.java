package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeParser;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;

public enum SmithingTrimRecipeParser implements RecipeParser<SmithingRecipe> {

    INSTANCE;

    @Override
    public @NotNull SmithingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String baseId = recipeConfig.getString("base");
            RecipeChoice base = BukkitRecipeChoiceParser.parseChoice(baseId);
            String additionId = recipeConfig.getString("addition");
            RecipeChoice addition = BukkitRecipeChoiceParser.parseChoice(additionId);
            String templateId = recipeConfig.getString("template");
            RecipeChoice template = BukkitRecipeChoiceParser.parseChoice(templateId);
            if (recipeConfig.isList("copy_components_rules")) {
                CopyComponentsManager.INSTANCE.addRecipeCopyNbtRules(recipeKey, recipeConfig.getStringList("copy_components_rules"));
            }
            return new SmithingTrimRecipe(recipeKey, template, base, addition, true);
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
