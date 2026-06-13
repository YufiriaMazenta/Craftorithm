package pers.yufiria.craftorithm.recipe.extra;

import io.papermc.paper.potion.PotionMix;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.CustomRecipe;

public class BrewingRecipe implements CustomRecipe {

    private PotionMix potionMix;

    public BrewingRecipe(PotionMix potionMix) {
        this.potionMix = potionMix;
    }

    public PotionMix potionMix() {
        return potionMix;
    }

    public BrewingRecipe setPotionMix(PotionMix potionMix) {
        this.potionMix = potionMix;
        return this;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return potionMix.getKey();
    }

    @Override
    public @NotNull ItemStack getResult() {
        return potionMix.getResult();
    }

    public @NotNull RecipeChoice input() {
        return potionMix.getInput();
    }

    public @NotNull RecipeChoice ingredient() {
        return potionMix.getIngredient();
    }

}
