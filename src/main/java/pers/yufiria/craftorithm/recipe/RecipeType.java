package pers.yufiria.craftorithm.recipe;

import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * 配方类型的接口,表示一种配方类型
 * 应为枚举或实现equals和hashcode方法以保证其唯一性
 */
public interface RecipeType {

    @NotNull String typeKey();

    @Range(from = 0, to = 256) int typeId();

    @NotNull RecipeLoader<?> recipeLoader();

    @NotNull RecipeRegister recipeRegister();

    boolean isThisType(Recipe recipe);

}
