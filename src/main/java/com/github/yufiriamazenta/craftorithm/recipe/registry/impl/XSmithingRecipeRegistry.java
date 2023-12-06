package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.DefRecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class XSmithingRecipeRegistry extends SmithingRecipeRegistry {

    private RecipeChoice template;
    private SmithingType smithingType;

    public XSmithingRecipeRegistry(@NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(namespacedKey, result);
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
                smithingRecipe = new SmithingRecipe(namespacedKey(), result(), base(), addition());
                break;
            case TRIM:
                smithingRecipe = new SmithingTrimRecipe(namespacedKey(), template, base(), addition());
                break;
            case TRANSFORM:
                Objects.requireNonNull(result(), "Recipe result cannot be null");
                smithingRecipe = new SmithingTransformRecipe(namespacedKey(), result(), template, base(), addition());
                break;
        }
        DefRecipeManager.INSTANCE.regRecipe(group(), smithingRecipe, RecipeType.SMITHING);
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
