package pers.yufiria.craftorithm.listener.hook;

import pers.yufiria.craftorithm.util.EventUtils;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.TimedRegisteredListener;
import org.jetbrains.annotations.NotNull;

public class RecipeCheckTimedRegisteredListener extends TimedRegisteredListener {

    public RecipeCheckTimedRegisteredListener(@NotNull Listener pluginListener, @NotNull EventExecutor eventExecutor, @NotNull EventPriority eventPriority, @NotNull Plugin registeredPlugin, boolean listenCancelled) {
        super(pluginListener, eventExecutor, eventPriority, registeredPlugin, listenCancelled);
    }

    @Override
    public void callEvent(@NotNull Event event) throws EventException {
        if (EventUtils.isCraftorithmRecipeEvent(event)) {
            //TODO 发送DEBUG信息
            return;
        }
        super.callEvent(event);
    }

}
