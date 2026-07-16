package pers.yufiria.craftorithm.recipe.choice;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * 可以用于注册到原版配方系统的配方材料，只有当启用nms配方注册时才能用于配方中
 * @param predicate
 * @param bukkitChoice
 */
@ApiStatus.Internal
public record PredicateChoice(
    Predicate<ItemStack> predicate,
    RecipeChoice bukkitChoice
) implements CustomRecipeChoice {

    /**
     * @param predicate    The predicate to test the crafting inputs to. Mutating
     *                     the ItemStack in the predicate is not supported.
     * @param bukkitChoice used to register into nms
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
