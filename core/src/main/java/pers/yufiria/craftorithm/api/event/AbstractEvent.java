package pers.yufiria.craftorithm.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class AbstractEvent extends Event {

    public AbstractEvent() {
    }

    public AbstractEvent(boolean isAsync) {
        super(isAsync);
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

}
