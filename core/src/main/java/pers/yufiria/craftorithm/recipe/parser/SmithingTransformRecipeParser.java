package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.List;

public enum SmithingTransformRecipeParser implements VanillaRecipeParser<SmithingRecipe> {

    INSTANCE;

    @Override
    public @NotNull SmithingRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId)).orElseThrow();
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String baseId = recipeConfig.getString("base");
            RecipeChoice base = choiceParser().parse(baseId);
            String additionId = recipeConfig.getString("addition");
            RecipeChoice addition = choiceParser().parse(additionId);
            if (recipeConfig.isList("copy_components_rules")) {
                List<String> keepNbtRules = recipeConfig.getStringList("copy_components_rules");
                CopyComponentsManager.INSTANCE.addRecipeCopyNbtRules(recipeKey, keepNbtRules);
            }
            String templateId = recipeConfig.getString("template");
            RecipeChoice template = choiceParser().parse(templateId);
            return new SmithingTransformRecipe(recipeKey, result, template, base, addition);
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }
}
