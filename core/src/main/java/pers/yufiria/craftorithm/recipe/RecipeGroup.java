package pers.yufiria.craftorithm.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecipeGroup {

    private final String groupId;
    //组内只做单条存取, 没有顺序语义, 用并发容器避免多区域线程同时注册/删除时破坏结构
    private final Map<NamespacedKey, Recipe> groupRecipes = new ConcurrentHashMap<>();

    public RecipeGroup(@NotNull String groupId) {
        this.groupId = groupId;
    }

    public String groupId() {
        return groupId;
    }

    public boolean contains(NamespacedKey recipeKey) {
        return groupRecipes.containsKey(recipeKey);
    }

    public boolean contains(Recipe recipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        return recipeKey != null && groupRecipes.containsKey(recipeKey);
    }

    public boolean isEmpty() {
        return groupRecipes.isEmpty();
    }

    public void addRecipe(@NotNull Recipe recipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            return;
        }
        groupRecipes.put(recipeKey, recipe);
    }

    public void removeRecipe(@NotNull Recipe recipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            return;
        }
        groupRecipes.remove(recipeKey);
    }

}
