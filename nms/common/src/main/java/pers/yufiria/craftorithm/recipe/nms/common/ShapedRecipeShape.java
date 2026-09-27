package pers.yufiria.craftorithm.recipe.nms.common;

import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 有序配方的形状数据：宽、高，以及按行优先展开的材料列表，空位为 {@link Optional#empty()}
 * <p>
 * 由 Bukkit 配方解析而来，供各版本的 NMS 配方实现共用
 */
public record ShapedRecipeShape(int width, int height, List<Optional<RecipeChoice>> ingredients) {

    /**
     * 解析 Bukkit 有序配方的形状与材料
     */
    public static ShapedRecipeShape of(ShapedRecipe recipe) {
        String[] shape = recipe.getShape();
        Map<Character, RecipeChoice> choiceMap = recipe.getChoiceMap();
        int height = shape.length;
        int width = 0;
        for (String line : shape) {
            width = Math.max(line.length(), width);
        }
        List<Optional<RecipeChoice>> ingredients = new ArrayList<>(width * height);
        for (String line : shape) {
            for (int column = 0; column < width; column++) {
                if (column >= line.length()) {
                    ingredients.add(Optional.empty());
                    continue;
                }
                char ingredientKey = line.charAt(column);
                if (ingredientKey == ' ') {
                    ingredients.add(Optional.empty());
                    continue;
                }
                ingredients.add(Optional.ofNullable(choiceMap.get(ingredientKey)));
            }
        }
        return new ShapedRecipeShape(width, height, ingredients);
    }

}