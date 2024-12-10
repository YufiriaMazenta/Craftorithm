package pers.yufiria.craftorithm.recipe.extra.anvil;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.jetbrains.annotations.NotNull;

public class PrepareAnvilRecipeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final AnvilRecipe anvilRecipe;
    private final PrepareAnvilEvent prepareAnvilEvent;

    public PrepareAnvilRecipeEvent(PrepareAnvilEvent event, AnvilRecipe anvilRecipe) {
        super(false);
        this.prepareAnvilEvent = event;
        this.anvilRecipe = anvilRecipe;
    }

    public AnvilRecipe anvilRecipe() {
        return anvilRecipe;
    }

    public PrepareAnvilEvent prepareAnvilEvent() {
        return prepareAnvilEvent;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

}
