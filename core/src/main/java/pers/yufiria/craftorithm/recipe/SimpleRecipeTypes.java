package pers.yufiria.craftorithm.recipe;

import crypticlib.MinecraftVersion;
import crypticlib.lang.node.StringLang;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.recipe.ingredient.RecipeChoiceParser;
import pers.yufiria.craftorithm.recipe.parser.*;
import pers.yufiria.craftorithm.recipe.register.AnvilRecipeRegistrar;
import pers.yufiria.craftorithm.recipe.register.BrewingRecipeRegistrar;
import pers.yufiria.craftorithm.recipe.register.BukkitRecipeRegistrar;
import pers.yufiria.craftorithm.recipe.register.RecipeRegistrar;

import java.util.Locale;
import java.util.function.Function;

public enum SimpleRecipeTypes implements RecipeType {

    UNKNOWN(
        "unknown",
        new RecipeParser<>() {
            @Override
            public @NotNull RecipeChoiceParser choiceParser() {
                return choiceStr -> null;
            }

            @Override
            public @Nullable Recipe parse(String recipeName, ConfigurationSection recipeConfig) {
                return null;
            }
        },
        new RecipeRegistrar() {
            @Override
            public boolean registerRecipe(Recipe recipe, boolean sync2Client) {
                return BukkitRecipeRegistrar.INSTANCE.registerRecipe(recipe, sync2Client);
            }

            @Override
            public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
                return BukkitRecipeRegistrar.INSTANCE.unregisterRecipe(recipeKey, sync2Client);
            }
        },
        recipe -> false,
        null,
        0),
    VANILLA_SHAPED(
        "vanilla_shaped",
        ShapedRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof ShapedRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SHAPED,
        1),
    VANILLA_SHAPELESS(
        "vanilla_shapeless",
        ShapelessRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof ShapelessRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SHAPELESS,
        2),
    VANILLA_SMELTING_FURNACE(
        "vanilla_smelting_furnace",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof FurnaceRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_FURNACE,
        3),
    VANILLA_SMELTING_BLAST(
        "vanilla_smelting_blast",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof BlastingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_BLAST,
        4),
    VANILLA_SMELTING_SMOKER(
        "vanilla_smelting_smoker",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof SmokingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_SMOKER,
        5),
    VANILLA_SMELTING_CAMPFIRE(
        "vanilla_smelting_campfire",
        SmeltingRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof CampfireRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMELTING_CAMPFIRE,
        6),
    VANILLA_SMITHING_TRANSFORM(
        "vanilla_smithing_transform",
        SmithingTransformRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof SmithingTransformRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMITHING_TRANSFORM,
        7),
    VANILLA_SMITHING_TRIM(
        "vanilla_smithing_trim",
        SmithingTrimRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof SmithingTrimRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_SMITHING_TRIM,
        8),
    VANILLA_STONECUTTING(
        "vanilla_stonecutting",
        StonecuttingRecipeParser.INSTANCE,
        BukkitRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof StonecuttingRecipe,
        Languages.RECIPE_TYPE_NAME_VANILLA_STONECUTTING,
        9),
    VANILLA_BREWING(
        "vanilla_brewing",
        BrewingRecipeParser.INSTANCE,
        BrewingRecipeRegistrar.INSTANCE,
        recipe -> {
            if (recipe instanceof BrewingRecipe) {
                return true;
            }
            if (MinecraftVersion.current().before(MinecraftVersion.V26_3))
                return false;
            return recipe instanceof org.bukkit.inventory.BrewingRecipe;
        },
        Languages.RECIPE_TYPE_NAME_VANILLA_BREWING,
        10),
    ANVIL(
        "anvil",
        AnvilRecipeParser.INSTANCE,
        AnvilRecipeRegistrar.INSTANCE,
        recipe -> recipe instanceof AnvilRecipe,
        Languages.RECIPE_TYPE_NAME_ANVIL,
        11);

    private final String typeKey;
    private final RecipeParser<?> recipeParser;
    private final RecipeRegistrar recipeRegistrar;
    private final Function<Recipe, Boolean> isThisTypeFunction;
    private final StringLang recipeNameLang;
    private final @Range(from = 0, to = 256) Integer typeId;

    SimpleRecipeTypes(
        String typeKey,
        RecipeParser<?> recipeParser,
        RecipeRegistrar recipeRegistrar,
        Function<Recipe, Boolean> isThisTypeFunction,
        StringLang recipeNameLang,
        @Range(from = 0, to = 256) Integer typeId
    ) {
        this.typeKey = typeKey;
        this.recipeParser = recipeParser;
        this.recipeRegistrar = recipeRegistrar;
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
    public @NotNull RecipeRegistrar recipeRegistrar() {
        return recipeRegistrar;
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
