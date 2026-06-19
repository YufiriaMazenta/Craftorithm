package pers.yufiria.craftorithm.api.event;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.RecipeRegister;
import pers.yufiria.craftorithm.recipe.RecipeType;

/**
 * 配方注册时触发的事件
 * 如果被取消则该配方不会注册
 */
public class RecipeRegisterEvent extends AbstractEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancel = false;
    private @NotNull final Recipe recipe;
    private @NotNull final NamespacedKey recipeKey;
    private @NotNull final RecipeType recipeType;
    private @NotNull RecipeRegister recipeRegister;

    public RecipeRegisterEvent(@NotNull Recipe recipe, @NotNull NamespacedKey recipeKey, @NotNull RecipeType recipeType, @NotNull RecipeRegister recipeRegister) {
        this.recipe = recipe;
        this.recipeKey = recipeKey;
        this.recipeType = recipeType;
        this.recipeRegister = recipeRegister;
    }

    public @NotNull Recipe recipe() {
        return recipe;
    }

    public @NotNull RecipeType recipeType() {
        return recipeType;
    }

    public @NotNull NamespacedKey recipeKey() {
        return recipeKey;
    }

    public void setRecipeRegister(@NotNull RecipeRegister recipeRegister) {
        this.recipeRegister = recipeRegister;
    }

    public @NotNull RecipeRegister recipeRegister() {
        return recipeRegister;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
