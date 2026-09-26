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
import pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public enum BrewingRecipeRegister implements RecipeRegister {

    INSTANCE;
    private final Map<NamespacedKey, BrewingRecipe> brewingRecipeMap = new ConcurrentHashMap<>();

    @Override
    public boolean registerRecipe(Recipe recipe, boolean updateRecipes) {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V26_3)) {
            // 26.3以上版本, 注册BrewingRecipe
            boolean result = CraftorithmRecipeRegistry.findImpl().registerRecipe(recipe, updateRecipes) == CraftorithmRecipeRegistry.RegisterResult.SUCCESS;
            if (result) {
                brewingRecipeMap.put(RecipeManager.INSTANCE.getRecipeKey(recipe), (BrewingRecipe) recipe);
            }
            return result;
        } else {
            // 26.2及以下版本, 注册PotionMix
            if (!RecipeManager.INSTANCE.supportBrewingRecipe()) {
                CrypticLib.info("&cThe server does not support brewing recipes");
                return false;
            }
            if (!(recipe instanceof BrewingRecipe brewingRecipe)) {
                return false;
            }
            PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
            PotionMix potionMix = (PotionMix) brewingRecipe.toPotionMix();
            brewingRecipeMap.put(brewingRecipe.getKey(), brewingRecipe);
            potionBrewer.addPotionMix(potionMix);
            return true;
        }
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V26_3)) {
            // 26.3以上版本, 走配方方式
            brewingRecipeMap.remove(recipeKey);
            return CraftorithmRecipeRegistry.findImpl().unregisterRecipe(recipeKey, updateRecipes);
        } else {
            // 26.2及以下版本, 走paper方式
            if (!RecipeManager.INSTANCE.supportBrewingRecipe()) {
                CrypticLib.info("&cThe server does not support brewing recipes");
                return false;
            }
            PotionBrewer potionBrewer = Bukkit.getPotionBrewer();
            potionBrewer.removePotionMix(recipeKey);
            brewingRecipeMap.remove(recipeKey);
            return true;
        }
    }

    public Optional<BrewingRecipe> match(ItemStack input, ItemStack ingredient) {
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V26_3)) {
            return Optional.empty();
        }
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

}
