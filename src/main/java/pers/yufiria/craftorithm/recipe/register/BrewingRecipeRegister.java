package pers.yufiria.craftorithm.recipe.register;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionBrewer;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;

public enum BrewingRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (!(recipe instanceof BrewingRecipe)) {
            return false;
        }
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        potionBrewer.addPotionMix(((BrewingRecipe) recipe).potionMix());
        return true;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        potionBrewer.removePotionMix(recipeKey);
        return true;
    }
}
