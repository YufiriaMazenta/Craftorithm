package pers.yufiria.craftorithm.recipe.register;

import crypticlib.CrypticLib;
import crypticlib.MinecraftVersion;
import io.papermc.paper.potion.PotionMix;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionBrewer;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.BrewingRecipe;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum BrewingRecipeRegister implements RecipeRegister {

    INSTANCE;
    private final Map<NamespacedKey, BrewingRecipe> brewingRecipeMap = new ConcurrentHashMap<>();
    private final RecipeRegister actualRegister = initActualRegister();

    @Override
    public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
        boolean result = actualRegister.registerRecipe(recipe, updateRecipes);
        if (result) {
            brewingRecipeMap.put(RecipeManager.INSTANCE.getRecipeKey(recipe), (BrewingRecipe) recipe);
        }

        return result;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        boolean result = actualRegister.unregisterRecipe(recipeKey, updateRecipes);
        if (result) {
            brewingRecipeMap.remove(recipeKey);
        }
        return result;
    }

    public Optional<BrewingRecipe> match(ItemStack input, ItemStack ingredient) {
        for (BrewingRecipe brewingRecipe : brewingRecipeMap.values()) {
            if (brewingRecipe.input().test(input) && brewingRecipe.ingredient().test(ingredient)) {
                return Optional.of(brewingRecipe);
            }
        }
        return Optional.empty();
    }

    public Optional<NamespacedKey> matchKey(ItemStack input, ItemStack ingredient) {
        for (Map.Entry<NamespacedKey, BrewingRecipe> entry : brewingRecipeMap.entrySet()) {
            BrewingRecipe brewingRecipe = entry.getValue();
            if (brewingRecipe.input().test(input) && brewingRecipe.ingredient().test(ingredient)) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    private RecipeRegister initActualRegister() {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V26_3)) {
            return Since26_3.INSTANCE;
        } else {
            return Before26_3.INSTANCE;
        }
    }

    /**
     * 用于26.3以上
     */
    enum Since26_3 implements RecipeRegister {

        INSTANCE;

        @Override
        public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
            return CraftorithmRecipeRegistry.findImpl().registerRecipe(recipe, updateRecipes) == CraftorithmRecipeRegistry.RegisterResult.SUCCESS;
        }

        @Override
        public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
            return CraftorithmRecipeRegistry.findImpl().unregisterRecipe(recipeKey, updateRecipes);
        }

    }

    enum Before26_3 implements RecipeRegister {

        INSTANCE;

        @Override
        public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
            if (!RecipeManager.INSTANCE.supportBrewingRecipe()) {
                CrypticLib.info("&cThe server does not support brewing recipes");
                return false;
            }
            if (!(recipe instanceof BrewingRecipe brewingRecipe)) {
                return false;
            }
            PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
            PotionMix potionMix = (PotionMix) brewingRecipe.toPotionMix();
            potionBrewer.addPotionMix(potionMix);
            return true;
        }

        @Override
        public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
            if (!RecipeManager.INSTANCE.supportBrewingRecipe()) {
                CrypticLib.info("&cThe server does not support brewing recipes");
                return false;
            }
            PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
            potionBrewer.removePotionMix(recipeKey);
            return true;
        }
    }

}
