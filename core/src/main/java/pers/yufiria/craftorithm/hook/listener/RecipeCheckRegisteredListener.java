package pers.yufiria.craftorithm.hook.listener;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.util.EventUtils;

import java.util.*;

public class RecipeCheckRegisteredListener extends RegisteredListener {

    private final UUID UNIQUE_KEY = UUID.randomUUID();

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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RecipeCheckRegisteredListener that)) return false;
        return Objects.equals(UNIQUE_KEY, that.UNIQUE_KEY);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(UNIQUE_KEY);
    }

}
