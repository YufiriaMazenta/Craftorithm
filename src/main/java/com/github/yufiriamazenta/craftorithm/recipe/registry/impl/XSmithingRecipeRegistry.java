package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class XSmithingRecipeRegistry extends SmithingRecipeRegistry {

    private RecipeChoice template;
    private SmithingType smithingType;

    public XSmithingRecipeRegistry(@NotNull String recipeGroup, @NotNull NamespacedKey namespacedKey, @Nullable ItemStack result) {
        super(recipeGroup, namespacedKey, result);
        this.smithingType = SmithingType.TRANSFORM;
    }

    public RecipeChoice template() {
        return template;
    }

    public XSmithingRecipeRegistry setTemplate(RecipeChoice template) {
        this.template = template;
        return this;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(base(), "Recipe base cannot be null");
        Objects.requireNonNull(addition(), "Recipe addition cannot be null");
        Objects.requireNonNull(template, "Recipe template cannot be null");

        SmithingRecipe smithingRecipe;
        switch (smithingType) {
            default:
                Objects.requireNonNull(result(), "Recipe result cannot be null");
                smithingRecipe = new SmithingRecipe(namespacedKey(), result(), base(), addition(), copyNbt);
                break;
            case TRIM:
                smithingRecipe = new SmithingTrimRecipe(namespacedKey(), template, base(), addition(), copyNbt);
                break;
            case TRANSFORM:
                Objects.requireNonNull(result(), "Recipe result cannot be null");
                smithingRecipe = new SmithingTransformRecipe(namespacedKey(), result(), template, base(), addition(), copyNbt);
                break;
        }
        RecipeManager.INSTANCE.regRecipe(group(), smithingRecipe, RecipeType.SMITHING);
    }

    public SmithingType smithingType() {
        return smithingType;
    }

    public XSmithingRecipeRegistry setSmithingType(SmithingType smithingType) {
        this.smithingType = smithingType;
        return this;
    }

    public enum SmithingType {
        TRIM, TRANSFORM
    }

}
