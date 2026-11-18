package pers.yufiria.craftorithm.api.event;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.register.RecipeRegistrar;

import java.util.Objects;

/**
 * 当从Craftorithm/recipes文件夹读取配方文件注册时触发
 * 如果被取消则该配方不会注册
 */
public class RecipeRegisterEvent extends Event implements EventCaller, Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private boolean cancel = false;
    private @NotNull Recipe recipe;
    private @NotNull
    final NamespacedKey recipeKey;
    private @NotNull
    final YamlConfiguration recipeConfig;
    private @NotNull RecipeRegistrar recipeRegistrar;
    private final Reason reason;

    public RecipeRegisterEvent(
        @NotNull Recipe recipe,
        @NotNull NamespacedKey recipeKey,
        @NotNull YamlConfiguration recipeConfig,
        @NotNull RecipeRegistrar recipeRegistrar,
        @NotNull Reason reason
    ) {
        this.recipe = Objects.requireNonNull(recipe);
        this.recipeKey = Objects.requireNonNull(recipeKey);
        this.recipeConfig = Objects.requireNonNull(recipeConfig);
        this.recipeRegistrar = Objects.requireNonNull(recipeRegistrar);
        this.reason = reason;
    }

    public void setRecipe(@NotNull Recipe recipe) {
        this.recipe = Objects.requireNonNull(recipe);
    }

    public @NotNull Recipe recipe() {
        return recipe;
    }

    public @NotNull NamespacedKey recipeKey() {
        return recipeKey;
    }

    public @NotNull YamlConfiguration recipeConfig() {
        return recipeConfig;
    }

    public void setRecipeRegister(@NotNull RecipeRegistrar recipeRegistrar) {
        this.recipeRegistrar = Objects.requireNonNull(recipeRegistrar);
    }

    public @NotNull RecipeRegistrar recipeRegister() {
        return recipeRegistrar;
    }

    public Reason reason() {
        return reason;
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

    public enum Reason {
        RELOAD,
        NEW_RECIPE,
        EDIT_RECIPE
    }

}
