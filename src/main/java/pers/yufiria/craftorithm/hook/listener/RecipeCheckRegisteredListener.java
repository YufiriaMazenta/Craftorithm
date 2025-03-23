package pers.yufiria.craftorithm.hook.listener;

import pers.yufiria.craftorithm.util.EventUtils;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

public class RecipeCheckRegisteredListener extends RegisteredListener {

    public RecipeCheckRegisteredListener(@NotNull Listener listener, @NotNull EventExecutor executor, @NotNull EventPriority priority, @NotNull Plugin plugin, boolean ignoreCancelled) {
        super(listener, executor, priority, plugin, ignoreCancelled);
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
