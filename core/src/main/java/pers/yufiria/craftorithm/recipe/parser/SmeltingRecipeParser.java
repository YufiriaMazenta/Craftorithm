package pers.yufiria.craftorithm.recipe.parser;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.util.Objects;

public enum SmeltingRecipeParser implements VanillaRecipeParser<CookingRecipe<?>> {

    INSTANCE;

    //熔炉默认烧炼时间, 单位tick
    private static final int DEFAULT_FURNACE_TIME = 200;
    //高炉/烟熏炉/营火默认烧炼时间, 单位tick
    private static final int DEFAULT_FAST_COOKING_TIME = 100;

    @Override
    public @NotNull CookingRecipe<?> parse(String recipeName, ConfigurationSection recipeConfig) {
        try {
            String recipeTypeId = recipeConfig.getString("type");
            RecipeType recipeType = RecipeManager.INSTANCE.getRecipeTypeByKey(recipeTypeId);
            if (recipeType == null) {
                throw new RecipeLoadException(recipeTypeId + " is not a smelting recipe type.");
            }
            String resultId = recipeConfig.getString("result");
            ItemStack result = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(resultId)).orElseThrow(() -> new ItemNotFoundException("Item not found: " + resultId));
            String ingredientId = recipeConfig.getString("ingredient");
            RecipeChoice ingredient = choiceParser().parse(ingredientId);
            NamespacedKey recipeKey = new NamespacedKey(Craftorithm.instance(), recipeName);
            float exp = (float) recipeConfig.getDouble("exp", 0);
            int time;
            CookingRecipe<?> recipe;
            switch (recipeType) {
                case SimpleRecipeTypes.VANILLA_SMELTING_FURNACE -> {
                    time = recipeConfig.getInt("time", DEFAULT_FURNACE_TIME);
                    recipe = new FurnaceRecipe(recipeKey, result, ingredient, exp, time);
                }
                case SimpleRecipeTypes.VANILLA_SMELTING_BLAST -> {
                    time = recipeConfig.getInt("time", DEFAULT_FAST_COOKING_TIME);
                    recipe = new BlastingRecipe(recipeKey, result, ingredient, exp, time);
                }
                case SimpleRecipeTypes.VANILLA_SMELTING_SMOKER -> {
                    time = recipeConfig.getInt("time", DEFAULT_FAST_COOKING_TIME);
                    recipe = new SmokingRecipe(recipeKey, result, ingredient, exp, time);
                }
                case SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE -> {
                    time = recipeConfig.getInt("time", DEFAULT_FAST_COOKING_TIME);
                    recipe = new CampfireRecipe(recipeKey, result, ingredient, exp, time);
                }
                default -> throw new RecipeLoadException(recipeTypeId + " is not a smelting recipe.");
            }
            String group = recipeConfig.getString("group");
            if (group != null) {
                recipe.setGroup(group);
            }
            if (recipeConfig.contains("recipe_book_category")) {
                String categoryStr = Objects.requireNonNull(recipeConfig.getString("recipe_book_category")).toUpperCase();
                CookingBookCategory category = CookingBookCategory.valueOf(categoryStr);
                recipe.setCategory(category);
            }
            return recipe;
        } catch (RecipeLoadException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RecipeLoadException(throwable);
        }
    }

}
