package com.github.yufiriamazenta.craftorithm.recipe.loader.vanilla;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeLoader;
import com.github.yufiriamazenta.craftorithm.recipe.util.RecipeChoiceParser;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.Nullable;

public enum SmithingRecipeLoader implements RecipeLoader<SmithingRecipe> {

    INSTANCE;

    @Override
    public @Nullable SmithingRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        String resultId = recipeConfig.getString("result");
        ItemStack result = ItemManager.INSTANCE.matchItem(resultId);
        NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
        String baseId = recipeConfig.getString("base");
        RecipeChoice base = RecipeChoiceParser.parseChoice(baseId);
        String additionId = recipeConfig.getString("addition");
        RecipeChoice addition = RecipeChoiceParser.parseChoice(additionId);
        if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
            return new SmithingRecipe(key, result, base, addition);
        }
        String templateId = recipeConfig.getString("template");
        RecipeChoice template = RecipeChoiceParser.parseChoice(templateId);
        //todo copy_nbt和group之类的东西
        return new SmithingTransformRecipe(key, result, base, addition, template);
    }
}
