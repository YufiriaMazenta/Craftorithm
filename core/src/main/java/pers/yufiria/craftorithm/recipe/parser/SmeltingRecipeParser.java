package pers.yufiria.craftorithm.recipe.parser;

import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeParser;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.recipe.util.BukkitRecipeChoiceParser;

import java.util.Objects;

public enum SmeltingRecipeParser implements RecipeParser<CookingRecipe<?>> {

    INSTANCE;

    @Override
    public @NotNull CookingRecipe<?> parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            String recipeTypeId = recipeConfig.getString("type");
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipeTypeId);
            if (recipeType == null) {
                throw new RecipeLoadException(recipeTypeId + " is not a smelting recipe type.");
            }
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId));
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = BukkitRecipeChoiceParser.parseChoice(ingredientId);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            float exp = (float) recipeConfig.getDouble("exp", 0);
            int time;
            CookingRecipe<?> recipe;
            switch (recipeType) {
                case SimpleRecipeTypes.VANILLA_SMELTING_FURNACE -> {
                    time = recipeConfig.getInt("time", 200);
                    recipe = new FurnaceRecipe(recipeKey, result, ingredient, exp, time);
                }
                case SimpleRecipeTypes.VANILLA_SMELTING_BLAST -> {
                    time = recipeConfig.getInt("time", 100);
                    recipe = new BlastingRecipe(recipeKey, result, ingredient, exp, time);
                }
                case SimpleRecipeTypes.VANILLA_SMELTING_SMOKER -> {
                    time = recipeConfig.getInt("time", 100);
                    recipe = new SmokingRecipe(recipeKey, result, ingredient, exp, time);
                }
                case SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE -> {
                    time = recipeConfig.getInt("time", 100);
                    recipe = new CampfireRecipe(recipeKey, result, ingredient, exp, time);
                }
                default -> throw new RecipeLoadException(recipeTypeId + " is not a smelting recipe.");
            }
            String group = recipeConfig.getString("group");
            if (group != null) {
                recipe.setGroup(group);
            }
            if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_19_3)) {
                if (recipeConfig.contains("recipe_book_category")) {
                    String categoryStr = Objects.requireNonNull(recipeConfig.getString("recipe_book_category")).toUpperCase();
                    CookingBookCategory category = CookingBookCategory.valueOf(categoryStr);
                    recipe.setCategory(category);
                }
            }
            return recipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
