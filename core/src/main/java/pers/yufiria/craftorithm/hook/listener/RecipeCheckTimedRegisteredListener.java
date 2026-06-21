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

import java.util.*;

public class RecipeCheckTimedRegisteredListener extends TimedRegisteredListener {

    private final UUID UNIQUE_KEY = UUID.randomUUID();

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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RecipeCheckTimedRegisteredListener that)) return false;
        return Objects.equals(UNIQUE_KEY, that.UNIQUE_KEY);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(UNIQUE_KEY);
    }

}
