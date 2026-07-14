package pers.yufiria.craftorithm.api.event.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.api.event.EventCaller;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.Optional;

public class CraftItemByFingerEvent extends Event implements EventCaller {

    private final NamespacedKey recipeKey;
    private final CraftingInventory inventory;
    private final InventoryClickEvent originBukkitEvent;

    public CraftItemByFingerEvent(CraftingInventory craftingInventory, NamespacedKey recipeKey, InventoryClickEvent originBukkitEvent) {
        this.recipeKey = recipeKey;
        this.inventory = craftingInventory;
        this.originBukkitEvent = originBukkitEvent;
    }

    public InventoryClickEvent originBukkitEvent() {
        return originBukkitEvent;
    }

    public CraftingInventory inventory() {
        return inventory;
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
