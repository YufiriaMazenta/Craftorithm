package com.github.yufiriamazenta.craftorithm.hook.listener;

import com.github.yufiriamazenta.craftorithm.util.EventUtils;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeCheckRegisteredListener extends RegisteredListener {

    public RecipeCheckRegisteredListener(@NotNull Listener listener, @NotNull EventExecutor executor, @NotNull EventPriority priority, @NotNull Plugin plugin, boolean ignoreCancelled) {
        super(listener, executor, priority, plugin, ignoreCancelled);
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
