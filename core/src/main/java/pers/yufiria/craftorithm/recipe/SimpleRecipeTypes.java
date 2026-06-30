package pers.yufiria.craftorithm.recipe;

import crypticlib.MinecraftVersion;
import crypticlib.lang.entry.StringLangEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipeParser;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.recipe.parser.*;
import pers.yufiria.craftorithm.recipe.register.AnvilRecipeRegister;
import pers.yufiria.craftorithm.recipe.register.BrewingRecipeRegister;
import pers.yufiria.craftorithm.recipe.register.BukkitRecipeRegister;

import java.util.Locale;
import java.util.function.Function;

public enum SimpleRecipeTypes implements RecipeType {

    UNKNOWN(
        "unknown",
        (recipeName, recipeConfig) -> null,
        new RecipeRegister() {
            @Override
            public boolean registerRecipe(Recipe recipe) {
                return false;
            }

            @Override
            public boolean unregisterRecipe(NamespacedKey recipeKey) {
                return false;
            }
        },
        recipe -> false,
        null,
        0),
    VANILLA_SHAPED(
        "vanilla_shaped",
        ShapedRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof ShapedRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SHAPED,
        1),
    VANILLA_SHAPELESS(
        "vanilla_shapeless",
        ShapelessRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof ShapelessRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SHAPELESS,
        2),
    VANILLA_SMELTING_FURNACE(
        "vanilla_smelting_furnace",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof FurnaceRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_FURNACE,
        3),
    VANILLA_SMELTING_BLAST(
        "vanilla_smelting_blast",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof BlastingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_BLAST,
        4),
    VANILLA_SMELTING_SMOKER(
        "vanilla_smelting_smoker",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof SmokingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_SMOKER,
        5),
    VANILLA_SMELTING_CAMPFIRE(
        "vanilla_smelting_campfire",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof CampfireRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_CAMPFIRE,
        6),
    VANILLA_SMITHING_TRANSFORM(
        "vanilla_smithing_transform",
        SmithingTransformRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> {
            if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
                return recipe instanceof SmithingRecipe;
            }
            return recipe instanceof SmithingTransformRecipe;
        },
        Languages.RECIPE_TYPE_NAME_VANILLA_SMITHING_TRANSFORM,
        7),
    VANILLA_SMITHING_TRIM(
        "vanilla_smithing_trim",
        SmithingTrimRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> {
            if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
                return false;
            }
            return recipe instanceof SmithingTrimRecipe;
        },
        Languages.RECIPE_TYPE_NAME_VANILLA_SMITHING_TRIM,
        8),
    VANILLA_STONECUTTING(
        "vanilla_stonecutting",
        StonecuttingRecipeParser.INSTANCE,
        BukkitRecipeRegister.INSTANCE,
        recipe -> recipe instanceof StonecuttingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_STONECUTTING,
        9),
    VANILLA_BREWING(
        "vanilla_brewing",
        BrewingRecipeParser.INSTANCE,
        BrewingRecipeRegister.INSTANCE,
        recipe -> recipe instanceof BrewingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_BREWING,
        10),
    ANVIL(
        "anvil",
        AnvilRecipeParser.INSTANCE,
        AnvilRecipeRegister.INSTANCE,
        recipe -> recipe instanceof AnvilRecipe,
        Languages.RECIPE_TYPE_NAME_ANVIL,
        11);

    private final String typeKey;
    private final RecipeParser<?> recipeParser;
    private final RecipeRegister recipeRegister;
    private final Function<Recipe, Boolean> isThisTypeFunction;
    private final StringLangEntry recipeNameLang;
    private final @Range(from = 0, to = 256) Integer typeId;

    SimpleRecipeTypes(
        String typeKey,
        RecipeParser<?> recipeParser,
        RecipeRegister recipeRegister,
        Function<Recipe, Boolean> isThisTypeFunction,
        StringLangEntry recipeNameLang,
        @Range(from = 0, to = 256) Integer typeId
    ) {
        this.typeKey = typeKey;
        this.recipeParser = recipeParser;
        this.recipeRegister = recipeRegister;
        this.isThisTypeFunction = isThisTypeFunction;
        this.recipeNameLang = recipeNameLang;
        this.typeId = typeId;
    }

    @Override
    public @NotNull String typeKey() {
        return typeKey;
    }

    @Override
    public @Range(from = 0, to = 256) int typeId() {
        return typeId;
    }

    @Override
    public @NotNull RecipeParser<?> recipeParser() {
        return recipeParser;
    }

    @Override
    public @NotNull RecipeRegister recipeRegister() {
        return recipeRegister;
    }

    @Override
    public boolean isThisType(Recipe recipe) {
        return isThisTypeFunction.apply(recipe);
    }

    @Override
    public @Nullable String getLocalizedName(@NotNull Player player) {
        if (recipeNameLang != null) {
            return recipeNameLang.value(player);
        }
        return null;
    }

    @Override
    public @Nullable String getLocalizedName(@NotNull Locale locale) {
        if (recipeNameLang != null) {
            return recipeNameLang.value(locale);
        }
        return null;
    }

}
