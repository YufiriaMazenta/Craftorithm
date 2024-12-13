package pers.yufiria.craftorithm.recipe;

import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.recipe.loader.*;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.register.AnvilRecipeRegister;
import pers.yufiria.craftorithm.recipe.register.BrewingRecipeRegister;
import pers.yufiria.craftorithm.recipe.register.VanillaRecipeRegister;

import java.util.function.Function;

public enum SimpleRecipeTypes implements RecipeType {

    VANILLA_SHAPED("vanilla_shaped", ShapedRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof ShapedRecipe),
    VANILLA_SHAPELESS("vanilla_shapeless", ShapelessRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof ShapelessRecipe),
    VANILLA_SMELTING_FURNACE("vanilla_smelting_furnace", SmeltingRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof FurnaceRecipe),
    VANILLA_SMELTING_BLAST("vanilla_smelting_blast", SmeltingRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof BlastingRecipe),
    VANILLA_SMELTING_SMOKER("vanilla_smelting_smoker", SmeltingRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof SmokingRecipe),
    VANILLA_SMELTING_CAMPFIRE("vanilla_smelting_campfire", SmeltingRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof CampfireRecipe),
    VANILLA_SMITHING_TRANSFORM("vanilla_smithing_transform", SmithingTransformRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> {
        if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
            return recipe instanceof SmithingRecipe;
        }
        return recipe instanceof SmithingTransformRecipe;
    }),
    VANILLA_SMITHING_TRIM("vanilla_smithing_trim", SmithingTrimRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof SmithingTrimRecipe),
    VANILLA_STONECUTTING("vanilla_stonecutting", StonecuttingRecipeLoader.INSTANCE, VanillaRecipeRegister.INSTANCE, recipe -> recipe instanceof StonecuttingRecipe),
    VANILLA_BREWING("vanilla_brewing", BrewingRecipeLoader.INSTANCE, BrewingRecipeRegister.INSTANCE, recipe -> recipe instanceof BrewingRecipe),
    ANVIL("anvil", AnvilRecipeLoader.INSTANCE, AnvilRecipeRegister.INSTANCE, recipe -> recipe instanceof AnvilRecipe),
    UNKNOWN("unknown", new RecipeLoader<>() {
        @Override
        public @Nullable Recipe loadRecipe(String recipeKey, ConfigurationSection recipeConfig) {
            return null;
        }
    }, new RecipeRegister() {
        @Override
        public boolean registerRecipe(Recipe recipe) {
            return false;
        }

        @Override
        public boolean unregisterRecipe(NamespacedKey recipeKey) {
            return false;
        }
    }, recipe -> false);

    private final String typeId;
    private final RecipeLoader<?> recipeLoader;
    private final RecipeRegister recipeRegister;
    private final Function<Recipe, Boolean> isThisTypeFunction;

    SimpleRecipeTypes(String typeId, RecipeLoader<?> recipeLoader, RecipeRegister recipeRegister, Function<Recipe, Boolean> isThisTypeFunction) {
        this.typeId = typeId;
        this.recipeLoader = recipeLoader;
        this.recipeRegister = recipeRegister;
        this.isThisTypeFunction = isThisTypeFunction;
    }

    @Override
    public @NotNull String typeId() {
        return typeId;
    }

    @Override
    public @NotNull RecipeLoader<?> recipeLoader() {
        return recipeLoader;
    }

    @Override
    public @NotNull RecipeRegister recipeRegister() {
        return recipeRegister;
    }

    @Override
    public boolean isThisType(Recipe recipe) {
        return isThisTypeFunction.apply(recipe);
    }

}
