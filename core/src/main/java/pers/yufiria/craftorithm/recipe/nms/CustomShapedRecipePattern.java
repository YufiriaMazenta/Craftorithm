package pers.yufiria.craftorithm.recipe.nms;

import org.bukkit.inventory.RecipeChoice;

import java.util.List;
import java.util.Optional;

public abstract class CustomShapedRecipePattern<Input> {

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

    protected static void shrink(String[] array) {
        if (array == null || array.length == 0) return;

        int maxLen = 0;
        for (String s : array) {
            int len = (s == null) ? 0 : s.length();
            if (len > maxLen) maxLen = len;
        }

        for (int i = 0; i < array.length; i++) {
            String s = array[i];
            if (s == null) {
                array[i] = " ".repeat(maxLen);  // Java 11+ 支持 repeat
            } else if (s.length() < maxLen) {
                // 右侧填充空格（左对齐）
                array[i] = s + " ".repeat(maxLen - s.length());
                // 如果右对齐，改成： " ".repeat(maxLen - s.length()) + s
            }
        }
    }

    public abstract boolean matches(Input input);

    protected abstract boolean matches(Input input, boolean symmetrical);

}
