package com.github.yufiriamazenta.craftorithm.recipe;

import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.recipe.custom.AnvilRecipe;
import com.github.yufiriamazenta.craftorithm.recipe.custom.PotionMixRecipe;
import crypticlib.CrypticLib;
import crypticlib.chat.entry.StringLangConfigEntry;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class RecipeType {

    private static final Map<String, RecipeType> BY_NAME = new ConcurrentHashMap<>();

    public static RecipeType SHAPED = new RecipeType(
        "shaped",
        Languages.RECIPE_TYPE_NAME_SHAPED,
        Bukkit::addRecipe,
        RecipeManager.INSTANCE::removeRecipes
    );
    public static RecipeType SHAPELESS = new RecipeType(
        "shapeless",
        Languages.RECIPE_TYPE_NAME_SHAPELESS,
        Bukkit::addRecipe,
        RecipeManager.INSTANCE::removeRecipes
    );
    public static RecipeType COOKING = new RecipeType(
        "cooking",
        Languages.RECIPE_TYPE_NAME_COOKING,
        recipe -> {
            if (CrypticLib.minecraftVersion() >= 11400) {
                Bukkit.addRecipe(recipe);
            }
        },
        RecipeManager.INSTANCE::removeRecipes
    );
    public static RecipeType SMITHING = new RecipeType(
        "smithing",
        Languages.RECIPE_TYPE_NAME_SMITHING,
        recipe -> {
            if (CrypticLib.minecraftVersion() >= 11400) {
                Bukkit.addRecipe(recipe);
            }
        },
        RecipeManager.INSTANCE::removeRecipes
    );
    public static RecipeType STONE_CUTTING = new RecipeType(
        "stone_cutting",
        Languages.RECIPE_TYPE_NAME_STONE_CUTTING,
        recipe -> {
            if (CrypticLib.minecraftVersion() >= 11400) {
                Bukkit.addRecipe(recipe);
            }
        },
        RecipeManager.INSTANCE::removeRecipes
    );
    public static RecipeType RANDOM_COOKING = new RecipeType(
        "random_cooking",
        Languages.RECIPE_TYPE_NAME_COOKING,
        recipe -> {
            if (CrypticLib.minecraftVersion() >= 11700) {
                Bukkit.addRecipe(recipe);
            }
        },
        RecipeManager.INSTANCE::removeRecipes
    );
    public static RecipeType UNKNOWN = new RecipeType(
        "unknown",
        null,
        recipe -> {},
        keys -> {}
    );
    public static RecipeType POTION = new RecipeType(
        "potion",
        Languages.RECIPE_TYPE_NAME_POTION,
        recipe -> {
            if (!RecipeManager.INSTANCE.supportPotionMix())
                return;
            Bukkit.getPotionBrewer().addPotionMix(((PotionMixRecipe) recipe).potionMix());
            RecipeManager.INSTANCE.potionMixRecipeMap().put(((PotionMixRecipe) recipe).key(), (PotionMixRecipe) recipe);
        },
        keys -> {
            if (!RecipeManager.INSTANCE.supportPotionMix())
                return;
            for (NamespacedKey recipeKey : keys) {
                Bukkit.getPotionBrewer().removePotionMix(recipeKey);
                RecipeManager.INSTANCE.potionMixRecipeMap().remove(recipeKey);
            }
        }
    );
    public static RecipeType ANVIL = new RecipeType(
        "anvil",
        Languages.RECIPE_TYPE_NAME_ANVIL,
        recipe -> {
            if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
                return;
            RecipeManager.INSTANCE.anvilRecipeMap().put(RecipeManager.INSTANCE.getRecipeKey(recipe), (AnvilRecipe) recipe);
        },
        keys -> {
            if (!PluginConfigs.ENABLE_ANVIL_RECIPE.value())
                return;
            for (NamespacedKey key : keys) {
                RecipeManager.INSTANCE.anvilRecipeMap().remove(key);
            }
        }
    );

    private final String typeId;
    private StringLangConfigEntry typeName;
    private Consumer<Recipe> register;
    private Consumer<List<NamespacedKey>> remover;

    private RecipeType(@NotNull String typeId, StringLangConfigEntry typeName, @NotNull Consumer<Recipe> register, @NotNull Consumer<List<NamespacedKey>> remover) {
        this.typeId = typeId.toUpperCase();
        this.typeName = typeName;
        this.register = register;
        this.remover = remover;
        BY_NAME.put(this.typeId, this);
    }

    public String typeId() {
        return typeId;
    }

    public StringLangConfigEntry typeName() {
        return typeName;
    }

    public RecipeType setTypeName(StringLangConfigEntry typeName) {
        this.typeName = typeName;
        return this;
    }

    public Consumer<Recipe> register() {
        return register;
    }

    public RecipeType setRegister(Consumer<Recipe> register) {
        this.register = register;
        return this;
    }

    public Consumer<List<NamespacedKey>> remover() {
        return remover;
    }

    public RecipeType setRemover(Consumer<List<NamespacedKey>> remover) {
        this.remover = remover;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecipeType that = (RecipeType) o;

        return typeId.equals(that.typeId);
    }

    @Override
    public int hashCode() {
        return typeId.hashCode();
    }

    public static RecipeType getByName(String typeName) {
        return BY_NAME.get(typeName.toUpperCase());
    }

    public static RecipeType[] types() {
        return BY_NAME.values().toArray(new RecipeType[0]);
    }

}
