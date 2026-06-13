package pers.yufiria.craftorithm.hook.listener;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.TimedRegisteredListener;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeCheckTimedRegisteredListener extends TimedRegisteredListener {

    public RecipeCheckTimedRegisteredListener(@NotNull Listener pluginListener, @NotNull EventExecutor eventExecutor, @NotNull EventPriority eventPriority, @NotNull Plugin registeredPlugin, boolean listenCancelled) {
        super(pluginListener, eventExecutor, eventPriority, registeredPlugin, listenCancelled);
    }

    @Override
    public void callEvent(@NotNull Event event) throws EventException {
        if (EventUtils.isCraftorithmRecipeEvent(event)) {
            return;
        }
        try {
            super.callEvent(event);
        } catch (Throwable throwable) {
            StackTraceElement[] stackTraceElements = throwable.getStackTrace();
            List<StackTraceElement> stackTraceElementList = new ArrayList<>(Arrays.asList(stackTraceElements));
            stackTraceElementList.removeIf(stackTraceElement -> stackTraceElement.getClassName().equals(this.getClass().getName()));
            throwable.setStackTrace(stackTraceElementList.toArray(new StackTraceElement[0]));
            throw throwable;
        }
    }

}
