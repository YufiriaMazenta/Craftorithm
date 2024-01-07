package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CookingRecipeRegistry extends RecipeRegistry {

    private RecipeChoice source;
    private int time;
    private float exp;
    private CookingBlock cookingBlock;


    public CookingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull ItemStack result) {
        super(group, namespacedKey, result);
        this.time = 200;
        this.exp = 0;
    }

    @Override
    public void register() {
        Objects.requireNonNull(namespacedKey(), "Recipe key cannot be null");
        Objects.requireNonNull(result(), "Recipe key cannot be null");
        Objects.requireNonNull(source, "Recipe ingredient cannot be null");
        CookingRecipe<?> cookingRecipe;
        switch (cookingBlock) {
            case FURNACE:
            default:
                cookingRecipe = new FurnaceRecipe(namespacedKey(), result(), source, exp, time);
                break;
            case SMOKER:
                cookingRecipe = new SmokingRecipe(namespacedKey(), result(), source, exp, time);
                break;
            case BLAST_FURNACE:
                cookingRecipe = new BlastingRecipe(namespacedKey(), result(), source, exp, time);
                break;
            case CAMPFIRE:
                cookingRecipe = new CampfireRecipe(namespacedKey(), result(), source, exp, time);
                break;
        }
        cookingRecipe.setGroup(group());
        RecipeManager.INSTANCE.regRecipe(group(), cookingRecipe, RecipeType.COOKING);
    }

    public RecipeChoice source() {
        return source;
    }

    public CookingRecipeRegistry setSource(RecipeChoice source) {
        this.source = source;
        return this;
    }

    public int time() {
        return time;
    }

    public CookingRecipeRegistry setTime(int time) {
        this.time = time;
        return this;
    }

    public float exp() {
        return exp;
    }

    public CookingRecipeRegistry setExp(float exp) {
        this.exp = exp;
        return this;
    }

    public CookingBlock cookingBlock() {
        return cookingBlock;
    }

    public CookingRecipeRegistry setCookingBlock(String cookingBlock) {
        this.cookingBlock = CookingBlock.valueOf(cookingBlock.toUpperCase());
        return this;
    }

    public CookingRecipeRegistry setCookingBlock(CookingBlock cookingBlock) {
        this.cookingBlock = cookingBlock;
        return this;
    }

    public enum CookingBlock {
        FURNACE(Material.FURNACE),
        BLAST_FURNACE(Material.BLAST_FURNACE),
        SMOKER(Material.SMOKER),
        CAMPFIRE(Material.CAMPFIRE);

        private final Material blockMaterial;

        CookingBlock(Material blockMaterial) {
            this.blockMaterial = blockMaterial;
        }

        public Material blockMaterial() {
            return blockMaterial;
        }

    }


}
