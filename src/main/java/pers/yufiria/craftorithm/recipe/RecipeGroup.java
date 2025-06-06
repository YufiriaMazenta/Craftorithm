package pers.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class RecipeGroup {

    private final String groupId;
    private final LinkedHashMap<NamespacedKey, Recipe> groupRecipes = new LinkedHashMap<>();

    public RecipeGroup(@NotNull String groupId) {
        this.groupId = groupId;
    }

    public String groupId() {
        return groupId;
    }

    public boolean contains(NamespacedKey namespacedKey) {
        return groupRecipes.containsKey(namespacedKey);
    }

    public boolean contains(Recipe recipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        return groupRecipes.containsKey(recipeKey);
    }

    public boolean isEmpty() {
        return groupRecipes.isEmpty();
    }

    public void addRecipe(@NotNull Recipe recipe) {
        groupRecipes.put(RecipeManager.INSTANCE.getRecipeKey(recipe), recipe);
    }

    public void removeRecipe(@NotNull Recipe recipe) {
        groupRecipes.remove(RecipeManager.INSTANCE.getRecipeKey(recipe));
    }

}
