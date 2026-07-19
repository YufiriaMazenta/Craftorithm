package pers.yufiria.craftorithm.recipe.parser;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

public enum SmithingTrimRecipeParser implements VanillaRecipeParser<SmithingRecipe> {

    INSTANCE;

    @Override
    @SuppressWarnings({"removal", "deprecation"})
    public @NotNull SmithingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String baseId = recipeConfig.getString("base");
            RecipeChoice base = choiceParser().parse(baseId);
            String additionId = recipeConfig.getString("addition");
            RecipeChoice addition = choiceParser().parse(additionId);
            String templateId = recipeConfig.getString("template");
            RecipeChoice template = choiceParser().parse(templateId);
            if (recipeConfig.isList("copy_components_rules")) {
                CopyComponentsManager.INSTANCE.addRecipeCopyNbtRules(recipeKey, recipeConfig.getStringList("copy_components_rules"));
            }
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21_5)) {
                String trimPatternKeyStr = recipeConfig.getString("trim_pattern");
                if (trimPatternKeyStr == null) {
                    throw new RecipeLoadException("Unable to find trim pattern from " + recipeName);
                }
                NamespacedKey trimPatternKey = NamespacedKey.fromString(trimPatternKeyStr);
                if (trimPatternKey == null) {
                    throw new RecipeLoadException("Trim pattern key is invalid: " + trimPatternKeyStr);
                }
                TrimPattern trimPattern = Registry.TRIM_PATTERN.get(trimPatternKey);
                if (trimPattern == null) {
                    throw new RecipeLoadException("Unknown trim pattern: " + trimPatternKeyStr);
                }
                return new SmithingTrimRecipe(recipeKey, template, base, addition, trimPattern);
            } else {
                return new SmithingTrimRecipe(recipeKey, template, base, addition);
            }
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
