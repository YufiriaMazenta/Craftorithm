package pers.yufiria.craftorithm.api.event.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.api.event.EventCaller;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.Optional;

public class PrepareItemCraftByFingerEvent extends Event implements EventCaller {

    private final PrepareItemCraftEvent originBukkitEvent;
    private final NamespacedKey recipeKey;

    public PrepareItemCraftByFingerEvent(PrepareItemCraftEvent originBukkitEvent, NamespacedKey recipeKey) {
        this.originBukkitEvent = originBukkitEvent;
        this.recipeKey = recipeKey;
    }

    public PrepareItemCraftEvent originBukkitEvent() {
        return originBukkitEvent;
    }

    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    public Optional<Recipe> recipe() {
        return Optional.ofNullable(RecipeManager.INSTANCE.getRecipe(recipeKey));
    }

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
