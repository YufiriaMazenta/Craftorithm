package pers.yufiria.craftorithm.recipe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

/**
 * 配方类型的接口,表示一种配方类型
 * 应为枚举或实现equals和hashcode方法以保证其唯一性
 */
public interface RecipeType {

    @NotNull String typeKey();

    @Range(from = 0, to = 256) int typeId();

    @NotNull RecipeParser<?> recipeParser();

    @NotNull RecipeRegister recipeRegister();

    boolean isThisType(Recipe recipe);

    @Nullable String getLocalizedName(@NotNull Player player);

    @Nullable String getLocalizedName(@NotNull Locale locale);

    default String getLocalizedName() {
        return getLocalizedName(Locale.getDefault());
    }

}
