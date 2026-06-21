package pers.yufiria.craftorithm.hook.listener;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

public record ConvertedRegisteredListener(
    String listenerClassName,
    RegisteredListener originRegisteredListener,
    RegisteredListener recipeCheckRegisteredListener,
    HandlerList handlerList
) {
}
