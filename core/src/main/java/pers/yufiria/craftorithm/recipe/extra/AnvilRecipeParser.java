package pers.yufiria.craftorithm.recipe.extra;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeParser;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

public enum AnvilRecipeParser implements RecipeParser<AnvilRecipe> {

    INSTANCE;

    @Override
    public @NotNull AnvilRecipe parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            String resultId = recipeConfig.getString("result");
            NamespacedItemIdStack result = NamespacedItemIdStack.fromString(resultId);
            String baseId = recipeConfig.getString("base");
            StackableItemIdChoice base = new StackableItemIdChoice(baseId);
            String additionId = recipeConfig.getString("addition");
            StackableItemIdChoice addition = new StackableItemIdChoice(additionId);
            int costLevel = recipeConfig.getInt("cost_level", 0);
            if (recipeConfig.isList("copy_components_rules")) {
                CopyComponentsManager.INSTANCE.addRecipeCopyNbtRules(recipeKey, recipeConfig.getStringList("copy_components_rules"));
            }
            AnvilRecipe anvilRecipe = new AnvilRecipe(recipeKey, result, base, addition);
            anvilRecipe.setCostLevel(costLevel);
            return anvilRecipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
