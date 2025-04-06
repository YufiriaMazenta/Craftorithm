package pers.yufiria.craftorithm.recipe.loader;

import crypticlib.MinecraftVersion;
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
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.copyComponents.CopyComponentsManager;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;

import java.util.List;

public enum SmithingTransformRecipeLoader implements RecipeLoader<SmithingRecipe> {

    INSTANCE;

    @Override
    public @NotNull SmithingRecipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
        try {
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId));
            NamespacedKey key = new NamespacedKey(Craftorithm.instance(), recipeKey);
            String baseId = recipeConfig.getString("base");
            RecipeChoice base = BukkitRecipeChoiceParser.parseChoice(baseId);
            String additionId = recipeConfig.getString("addition");
            RecipeChoice addition = BukkitRecipeChoiceParser.parseChoice(additionId);
            if (recipeConfig.isList("copy_components_rules")) {
                List<String> keepNbtRules = recipeConfig.getStringList("copy_components_rules");
                CopyComponentsManager.INSTANCE.addRecipeCopyNbtRules(key, keepNbtRules);
            }
            SmithingRecipe recipe;
            if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
                recipe = new SmithingRecipe(key, result, base, addition);
            } else {
                String templateId = recipeConfig.getString("template");
                RecipeChoice template = BukkitRecipeChoiceParser.parseChoice(templateId);
                recipe = new SmithingTransformRecipe(key, result, base, addition, template);
            }
            return recipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }
}
