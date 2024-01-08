package com.github.yufiriamazenta.craftorithm.recipe.registry;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StoneCuttingRecipeRegistry extends UnlockableRecipeRegistry {

    private List<ItemStack> results;
    private List<RecipeChoice> ingredientList = new CopyOnWriteArrayList<>();
    private final Map<NamespacedKey, Recipe> subRecipes = new ConcurrentHashMap<>();
    private final Map<String, NamespacedKey> subRecipeKeys = new ConcurrentHashMap<>();

    public StoneCuttingRecipeRegistry(@NotNull String group, @NotNull NamespacedKey namespacedKey, @NotNull List<ItemStack> results) {
        super(group, namespacedKey, results.get(0));
        this.results = results;
        String keyStr = namespacedKey.getKey();
        for (int i = 0; i < ingredientList.size(); i++) {
            RecipeChoice ingredient = ingredientList.get(i);
            for (int j = 0; j < results.size(); j++) {
                ItemStack result = results.get(i);
                String subKeyStr = keyStr + "." + i + "." + j;
                NamespacedKey subKey = new NamespacedKey(Craftorithm.instance(), subKeyStr);
                subRecipeKeys.put(subKeyStr, subKey);
                StonecuttingRecipe stonecuttingRecipe = new StonecuttingRecipe(subKey, result, ingredient);
                subRecipes.put(subKey, stonecuttingRecipe);
                stonecuttingRecipe.setGroup(group);
            }
        }
    }

    @Override
    public void register() {
        subRecipes.forEach(
            (key, recipe) -> {
                RecipeManager.INSTANCE.regRecipe(group(), recipe, RecipeType.STONE_CUTTING);
                RecipeManager.INSTANCE.recipeUnlockMap().put(key, unlock);
            }
        );
    }

    public List<RecipeChoice> ingredientList() {
        return ingredientList;
    }

    public StoneCuttingRecipeRegistry setIngredientList(List<RecipeChoice> ingredientList) {
        this.ingredientList = ingredientList;
        return this;
    }

    public List<ItemStack> results() {
        return results;
    }

    public StoneCuttingRecipeRegistry setResults(List<ItemStack> results) {
        this.results = results;
        return this;
    }

    public Map<NamespacedKey, Recipe> subRecipeMap() {
        return subRecipes;
    }

    public Map<String, NamespacedKey> subRecipeKeyMap() {
        return subRecipeKeys;
    }

    @Override
    public RecipeType recipeType() {
        return RecipeType.STONE_CUTTING;
    }

}
