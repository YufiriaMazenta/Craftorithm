package pers.yufiria.craftorithm.recipe.register;

import crypticlib.util.IOHelper;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionBrewer;
import pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum BrewingRecipeRegister implements RecipeRegister {

    INSTANCE;
    private final Map<NamespacedKey, PotionMix> potionMixMap = new ConcurrentHashMap<>();

    @Override
    public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            IOHelper.info("&cThe server does not support brewing recipes");
            return false;
        }
        if (!(recipe instanceof BrewingRecipe brewingRecipe)) {
            return false;
        }
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        PotionMix potionMix = brewingRecipe.toPotionMix();
        potionMixMap.put(brewingRecipe.getKey(), potionMix);
        potionBrewer.addPotionMix(potionMix);
        return true;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        if (!RecipeManager.INSTANCE.supportPotionMix()) {
            IOHelper.info("&cThe server does not support brewing recipes");
            return false;
        }
        PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
        potionBrewer.removePotionMix(recipeKey);
        potionMixMap.remove(recipeKey);
        return true;
    }

    public Optional<PotionMix> mix(ItemStack input, ItemStack ingredient) {
        for (PotionMix potionMix : potionMixMap.values()) {
            if (potionMix.getInput().test(input) && potionMix.getIngredient().test(ingredient)) {
                return Optional.of(potionMix);
            }
        }
        return Optional.empty();
    }

    public Optional<NamespacedKey> mixKey(ItemStack input, ItemStack ingredient) {
        for (Map.Entry<NamespacedKey, PotionMix> entry : potionMixMap.entrySet()) {
            if (entry.getValue().getInput().test(input) && entry.getValue().getIngredient().test(ingredient)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

}
