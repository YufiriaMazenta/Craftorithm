package pers.yufiria.craftorithm.recipe.loader;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;

public enum SmithingTrimRecipeLoader implements RecipeLoader<SmithingRecipe> {

    INSTANCE;

    @Override
    public @NotNull SmithingRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            String baseId = recipeConfig.getString("base");
            RecipeChoice base = BukkitRecipeChoiceParser.parseChoice(baseId);
            String additionId = recipeConfig.getString("addition");
            RecipeChoice addition = BukkitRecipeChoiceParser.parseChoice(additionId);
            String templateId = recipeConfig.getString("template");
            RecipeChoice template = BukkitRecipeChoiceParser.parseChoice(templateId);
            if (recipeConfig.isList("copy_components_rules")) {
                CopyComponentsManager.INSTANCE.addRecipeCopyNbtRules(key, recipeConfig.getStringList("copy_components_rules"));
            }
            return new SmithingTrimRecipe(key, template, base, addition, true);
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
