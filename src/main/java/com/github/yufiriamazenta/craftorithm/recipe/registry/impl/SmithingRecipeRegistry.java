package com.github.yufiriamazenta.craftorithm.recipe.registry.impl;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import com.github.yufiriamazenta.craftorithm.recipe.registry.RecipeRegistry;
import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SmithingRecipeRegistry extends RecipeRegistry {

    private RecipeChoice template;
    private SmithingType smithingType;
    private RecipeChoice base, addition;
    protected boolean copyNbt = false;
    protected boolean copyEnchantments = true;

    public SmithingRecipeRegistry(@NotNull String recipeGroup, @NotNull NamespacedKey namespacedKey, @Nullable ItemStack result) {
        super(recipeGroup, namespacedKey, result);
        this.smithingType = SmithingType.TRANSFORM;
    }

    public RecipeChoice template() {
        return template;
    }

    public SmithingRecipeRegistry setTemplate(RecipeChoice template) {
        this.template = template;
        return this;
    }

    public RecipeChoice base() {
        return base;
    }

    public SmithingRecipeRegistry setBase(RecipeChoice base) {
        this.base = base;
        return this;
    }

    public RecipeChoice addition() {
        return addition;
    }

    public SmithingRecipeRegistry setAddition(RecipeChoice addition) {
        this.addition = addition;
        return this;
    }

    public boolean copyNbt() {
        //因为1.20.6开始Paper对组件的处理与NBT不同，所以需要倒转此属性
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_5))
            return !copyNbt;
        else
            return copyNbt;
    }

    public SmithingRecipeRegistry setCopyNbt(boolean copyNbt) {
        this.copyNbt = copyNbt;
        return this;
    }

    public boolean copyEnchantments() {
        return copyEnchantments;
    }

    public SmithingRecipeRegistry setCopyEnchantments(boolean copyEnchantments) {
        this.copyEnchantments = copyEnchantments;
        return this;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(base(), "Recipe base cannot be null");
        Objects.requireNonNull(addition(), "Recipe addition cannot be null");
        Objects.requireNonNull(template, "Recipe template cannot be null");

        SmithingRecipe smithingRecipe;
        if (Objects.requireNonNull(smithingType) == SmithingType.TRIM) {
            if (CrypticLibBukkit.isPaper())
                smithingRecipe = new SmithingTrimRecipe(namespacedKey(), template, base(), addition(), copyNbt());
            else
                smithingRecipe = new SmithingTrimRecipe(namespacedKey(), template, base(), addition());
        } else {
            Objects.requireNonNull(result(), "Recipe result cannot be null");
            if (CrypticLibBukkit.isPaper())
                smithingRecipe = new SmithingTransformRecipe(namespacedKey(), result(), template, base(), addition(), copyNbt());
            else
                smithingRecipe = new SmithingTransformRecipe(namespacedKey(), result(), template, base(), addition());
        }

        RecipeManager.INSTANCE.regRecipe(group(), smithingRecipe, RecipeType.SMITHING);
    }

    public SmithingType smithingType() {
        return smithingType;
    }

    public SmithingRecipeRegistry setSmithingType(SmithingType smithingType) {
        this.smithingType = smithingType;
        return this;
    }

    public enum SmithingType {
        TRIM, TRANSFORM
    }

}
