package pers.yufiria.craftorithm.recipe.loader;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.keepNbt.KeepNbtManager;

public enum AnvilRecipeLoader implements RecipeLoader<AnvilRecipe> {

    INSTANCE;

    @Override
    public @NotNull AnvilRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey namespacedKey = new NamespacedKey(Craftorithm.instance(), recipeKey);
            String resultId = recipeConfig.getString("result");
            NamespacedItemIdStack result = NamespacedItemIdStack.fromString(resultId);
            String baseId = recipeConfig.getString("base");
            StackableItemIdChoice base = new StackableItemIdChoice(baseId);
            String additionId = recipeConfig.getString("addition");
            StackableItemIdChoice addition = new StackableItemIdChoice(additionId);
            int costLevel = recipeConfig.getInt("cost_level", 0);
            if (recipeConfig.isList("keep_nbt_rules")) {
                KeepNbtManager.INSTANCE.addRecipeKeepNbtRules(namespacedKey, recipeConfig.getStringList("keep_nbt_rules"));
            }
            AnvilRecipe anvilRecipe = new AnvilRecipe(namespacedKey, result, base, addition);
            anvilRecipe.setCostLevel(costLevel);
            return anvilRecipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
