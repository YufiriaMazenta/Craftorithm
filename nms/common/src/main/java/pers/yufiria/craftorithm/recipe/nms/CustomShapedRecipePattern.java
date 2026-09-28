package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * 有序配方的通用实现：包含形状、材料等信息
 * <p>
 * 各版本的配方实现只需要把 NMS 的合成输入内容转换为 Bukkit 物品列表
 * 匹配逻辑（1.20 ~ 1.20.5 的滑动匹配与 1.21 起的精确匹配）统一在这里
 */
public class CustomShapedRecipePattern {

    protected final int width;
    protected final int height;
    protected final int ingredientCount;
    protected final List<Optional<RecipeChoice>> ingredients;
    protected final boolean symmetrical;

    public CustomShapedRecipePattern(int width, int height, List<Optional<RecipeChoice>> ingredients) {
        this.width = width;
        this.height = height;
        this.ingredientCount = (int) ingredients.stream().flatMap(Optional::stream).count();
        this.ingredients = ingredients;
        this.symmetrical = isSymmetrical(width, height, ingredients);
    }

    /**
     * 解析 Bukkit 有序配方的形状与材料
     */
    public static CustomShapedRecipePattern fromBukkitRecipe(ShapedRecipe shapedRecipe) {
        ShapedRecipeShape shape = ShapedRecipeShape.of(shapedRecipe);
        return new CustomShapedRecipePattern(shape.width(), shape.height(), shape.ingredients());
    }

    public static <T> boolean isSymmetrical(int width, int height, List<T> list) {
        if (width != 1) {
            int var3 = width / 2;

            for (int var4 = 0; var4 < height; ++var4) {
                for (int var5 = 0; var5 < var3; ++var5) {
                    int var6 = width - 1 - var5;
                    T var7 = list.get(var5 + var4 * width);
                    T var8 = list.get(var6 + var4 * width);
                    if (!var7.equals(var8)) {
                        return false;
                    }
                }
            }

        }
        return true;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int ingredientCount() {
        return ingredientCount;
    }

    public List<Optional<RecipeChoice>> ingredients() {
        return ingredients;
    }

    public boolean symmetrical() {
        return symmetrical;
    }

    /**
     * 1.20 ~ 1.20.5 的匹配方式：在合成格内滑动寻找匹配位置，每个位置先按镜像再按正向匹配
     *
     * @param inputItems 行优先展开的 Bukkit 合成格物品
     */
    public boolean matchesBefore1_21(List<ItemStack> inputItems, int inputWidth, int inputHeight) {
        for (int offsetX = 0; offsetX <= inputWidth - width; offsetX++) {
            for (int offsetY = 0; offsetY <= inputHeight - height; offsetY++) {
                if (matchesBefore1_21(inputItems, inputWidth, inputHeight, offsetX, offsetY, true)) {
                    return true;
                }
                if (matchesBefore1_21(inputItems, inputWidth, inputHeight, offsetX, offsetY, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 1.21 起的匹配方式
     *
     * @param inputItems 行优先展开的 Bukkit 合成格物品
     */
    public boolean matchesSince1_21(List<ItemStack> inputItems, int inputWidth, int inputHeight, int inputIngredientCount) {
        if (inputIngredientCount != ingredientCount) {
            return false;
        }
        if (inputWidth != width || inputHeight != height) {
            return false;
        }
        if (!symmetrical && matchesSince1_21(inputItems, inputWidth, true)) {
            return true;
        }
        return matchesSince1_21(inputItems, inputWidth, false);
    }

    /**
     * 按宽高生成还原 Bukkit 配方时使用的 shape，材料字符从 a 开始按行优先排列
     *
     * @return 宽或高超出 3 时返回 null
     */
    public String[] shapeArray() {
        if (width < 1 || width > 3 || height < 1 || height > 3) {
            return null;
        }
        String[] shape = new String[height];
        for (int row = 0; row < height; row++) {
            StringBuilder line = new StringBuilder(width);
            for (int column = 0; column < width; column++) {
                line.append((char) ('a' + row * width + column));
            }
            shape[row] = line.toString();
        }
        return shape;
    }

    /**
     * 按 shape 中的字符顺序把材料设置到 Bukkit 配方上，空位跳过
     */
    public void applyIngredients(BiConsumer<Character, RecipeChoice> ingredientSetter) {
        char ingredientKey = 'a';
        for (Optional<RecipeChoice> ingredient : ingredients) {
            if (ingredient.isPresent()) {
                ingredientSetter.accept(ingredientKey, ingredient.get());
            }
            ingredientKey++;
        }
    }

    /**
     * 把 shape 中未定义材料的字符替换为空格，避免按缺失的字符去取材料
     */
    public static String[] replaceUndefinedIngredients(String[] shape, Map<Character, RecipeChoice> choiceMap) {
        String[] filteredShape = new String[shape.length];
        for (int row = 0; row < shape.length; row++) {
            String line = shape[row];
            StringBuilder filteredLine = new StringBuilder(line.length());
            for (char ingredientKey : line.toCharArray()) {
                filteredLine.append(choiceMap.get(ingredientKey) == null ? ' ' : ingredientKey);
            }
            filteredShape[row] = filteredLine.toString();
        }
        return filteredShape;
    }

    private boolean matchesBefore1_21(List<ItemStack> inputItems, int inputWidth, int inputHeight,
                                      int offsetX, int offsetY, boolean mirrored) {
        for (int column = 0; column < inputWidth; column++) {
            for (int row = 0; row < inputHeight; row++) {
                int patternColumn = column - offsetX;
                int patternRow = row - offsetY;
                Optional<RecipeChoice> ingredient = Optional.empty();
                if (patternColumn >= 0 && patternRow >= 0 && patternColumn < width && patternRow < height) {
                    ingredient = ingredients.get(ingredientIndex(patternColumn, patternRow, mirrored));
                }
                if (!IngredientUtils.testOptionalChoice(ingredient, inputItems.get(column + row * inputWidth))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean matchesSince1_21(List<ItemStack> inputItems, int inputWidth, boolean mirrored) {
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                Optional<RecipeChoice> ingredient = ingredients.get(ingredientIndex(column, row, mirrored));
                if (!IngredientUtils.testOptionalChoice(ingredient, inputItems.get(column + row * inputWidth))) {
                    return false;
                }
            }
        }
        return true;
    }

    private int ingredientIndex(int column, int row, boolean mirrored) {
        return mirrored ? width - column - 1 + row * width : column + row * width;
    }

}
