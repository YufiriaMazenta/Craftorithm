package pers.yufiria.craftorithm.recipe.loader;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.choice.StackableItemIdChoice;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.extra.anvil.AnvilRecipe;

public enum AnvilRecipeLoader implements RecipeLoader<AnvilRecipe> {

    INSTANCE;

    @Override
    public @NotNull AnvilRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            NamespacedKey namespacedKey = new NamespacedKey(recipeKey, "anvil");
            String resultId = recipeConfig.getString("result");
            NamespacedItemIdStack result = NamespacedItemIdStack.fromString(resultId);
            String baseId = recipeConfig.getString("base");
            StackableItemIdChoice base = new StackableItemIdChoice(baseId);
            String additionId = recipeConfig.getString("addition");
            StackableItemIdChoice addition = new StackableItemIdChoice(additionId);
            int costLevel = recipeConfig.getInt("cost_level", 0);
            boolean copyNbt = recipeConfig.getBoolean("copy_nbt", false);
            boolean copyEnchantments = recipeConfig.getBoolean("copy_enchantments", true);
            AnvilRecipe anvilRecipe = new AnvilRecipe(namespacedKey, result, base, addition);
            anvilRecipe.setCopyNbt(copyNbt);
            anvilRecipe.setCostLevel(costLevel);
            anvilRecipe.setCopyEnchantments(copyEnchantments);
            return anvilRecipe;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
