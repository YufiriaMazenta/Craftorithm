package me.yufiria.craftorithm.recipe.builder;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShapedRecipeBuilder extends AbstractRecipeBuilder {

    private String[] shape;
    private Map<Character, RecipeChoice> recipeChoiceMap;

    private ShapedRecipeBuilder() {}

    @Override
    public ShapedRecipeBuilder key(NamespacedKey key) {
        return (ShapedRecipeBuilder) super.key(key);
    }

    @Override
    public ShapedRecipeBuilder result(ItemStack result) {
        return (ShapedRecipeBuilder) super.result(result);
    }

    public String[] getShape() {
        return shape;
    }

    public ShapedRecipeBuilder shape(String[] shape) {
        this.shape = shape;
        return this;
    }

    public Map<Character, RecipeChoice> getRecipeChoiceMap() {
        return recipeChoiceMap;
    }

    public ShapedRecipeBuilder recipeChoiceMap(Map<Character, RecipeChoice> recipeChoiceMap) {
        this.recipeChoiceMap = recipeChoiceMap;
        return this;
    }

    public ShapedRecipe build() {
        ShapedRecipe shapedRecipe = new ShapedRecipe(getKey(), getResult());
        shapedRecipe.shape(shape);
        Set<Character> shapeStrChars = new HashSet<>();
        for (String s : shape) {
            for (char c : s.toCharArray()) {
                shapeStrChars.add(c);
            }
        }
        Set<Character> keySet = new HashSet<>(recipeChoiceMap.keySet());
        keySet.removeIf((character -> !shapeStrChars.contains(character)));
        for (Character ingredientKey : keySet) {
            shapedRecipe.setIngredient(ingredientKey, recipeChoiceMap.get(ingredientKey));
        }
        return shapedRecipe;
    }

    public static ShapedRecipeBuilder builder() {
        return new ShapedRecipeBuilder();
    }

}
