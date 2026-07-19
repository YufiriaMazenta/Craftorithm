package pers.yufiria.craftorithm.api.recipe.choice;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * 可以用于注册到原版配方系统的配方材料，通过传入Predicate来实现自定义的材料识别逻辑
 * @param predicate
 * @param bukkitChoice
 */
public record PredicateChoice(
    Predicate<ItemStack> predicate,
    RecipeChoice bukkitChoice
) implements CustomRecipeChoice {

    /**
     * @param predicate 配方材料的识别方式
     * @param bukkitChoice 用于原版配方的占位符，主要作用在配方书显示等方面，必须是{@link org.bukkit.inventory.RecipeChoice.MaterialChoice}或{@link org.bukkit.inventory.RecipeChoice.ExactChoice}
     */
    public PredicateChoice {
        Preconditions.checkArgument(predicate != null, "The item predicate cannot be null");
        Preconditions.checkArgument(bukkitChoice != null, "The example stack cannot be null");
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return this.bukkitChoice.getItemStack();
    }

    @Override
    public PredicateChoice clone() {
        return new PredicateChoice(this.predicate, this.bukkitChoice.clone());
    }

    @Override
    public boolean test(final @NotNull ItemStack itemStack) {
        return this.predicate.test(itemStack);
    }

}
