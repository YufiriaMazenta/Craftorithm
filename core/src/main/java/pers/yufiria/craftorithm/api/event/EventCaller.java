package pers.yufiria.craftorithm.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public interface EventCaller {

    default void call() {
        if (this instanceof Event event) {
            Bukkit.getPluginManager().callEvent(event);
        }
    }

}
