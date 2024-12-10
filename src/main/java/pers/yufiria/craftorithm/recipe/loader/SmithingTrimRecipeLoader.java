package pers.yufiria.craftorithm.recipe.loader;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeLoader;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;

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
            boolean copyNbt = recipeConfig.getBoolean("copy_nbt", false);
            //因为1.20.5开始,复制组件行为是使用结果的组件,而之前的行为是使用原材料的NBT,所以需要反转以逻辑相同
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5)) {
                copyNbt = !copyNbt;
            }
            return new SmithingTrimRecipe(key, template, base, addition, copyNbt);
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
