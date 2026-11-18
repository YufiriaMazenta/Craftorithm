package pers.yufiria.craftorithm.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Craftorithm重载完毕时触发的事件, 在异步线程触发
 */
public class CraftorithmReloadDoneEvent extends Event implements EventCaller {

    private static final HandlerList handlers = new HandlerList();

    @ApiStatus.Internal
    public CraftorithmReloadDoneEvent() {
        super(true);
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
