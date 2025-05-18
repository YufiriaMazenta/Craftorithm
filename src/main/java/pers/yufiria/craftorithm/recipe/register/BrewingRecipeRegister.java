package pers.yufiria.craftorithm.recipe.register;

import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionBrewer;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.util.ServerUtils;

public enum BrewingRecipeRegister implements RecipeRegister {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe recipe) {
        if (!ServerUtils.supportPotionMix()) {
            IOHelper.info("&cThe server does not support brewing recipes");
            return false;
        }
        if (!(recipe instanceof BrewingRecipe)) {
            return false;
        }
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        potionBrewer.addPotionMix(((BrewingRecipe) recipe).potionMix());
        return true;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey) {
        if (!ServerUtils.supportPotionMix()) {
            IOHelper.info("&cThe server does not support brewing recipes");
            return false;
        }
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        potionBrewer.removePotionMix(recipeKey);
        return true;
    }
}
