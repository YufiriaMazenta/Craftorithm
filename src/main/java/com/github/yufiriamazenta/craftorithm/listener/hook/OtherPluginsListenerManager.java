package com.github.yufiriamazenta.craftorithm.listener.hook;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.listener.EventListener;
import crypticlib.util.ReflectionHelper;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;

import java.lang.reflect.Field;

@EventListener
@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.RELOAD)
    }
)
public enum OtherPluginsListenerManager implements Listener, BukkitLifeCycleTask {

    INSTANCE;
    private final Field executorField = ReflectionHelper.getDeclaredField(RegisteredListener.class, "executor");

    public void convertOtherPluginsListeners() {
        for (HandlerList handlerList : HandlerList.getHandlerLists()) {
            for (RegisteredListener registeredListener : handlerList.getRegisteredListeners()) {
                if (registeredListener.getPlugin().equals(Craftorithm.instance())) continue;
                if (registeredListener instanceof RecipeCheckRegisteredListener || registeredListener instanceof RecipeCheckTimedRegisteredListener)
                    continue;

                handlerList.unregister(registeredListener);

                boolean handled = false;

                try {
                    if (registeredListener instanceof TimedRegisteredListener) {
                        handlerList.register(new RecipeCheckTimedRegisteredListener(registeredListener.getListener(), getRegisteredListenerExecutor(registeredListener), registeredListener.getPriority(), registeredListener.getPlugin(), registeredListener.isIgnoringCancelled()));
                        handled = true;
                    }
                } catch (Exception ignore) {
                }

                if (!handled) {
                    handlerList.register(new RecipeCheckRegisteredListener(registeredListener.getListener(), getRegisteredListenerExecutor(registeredListener), registeredListener.getPriority(), registeredListener.getPlugin(), registeredListener.isIgnoringCancelled()));
                }
            }
        }
    }

    public EventExecutor getRegisteredListenerExecutor(RegisteredListener registeredListener) {
        return ReflectionHelper.getDeclaredFieldObj(executorField, registeredListener);
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        convertOtherPluginsListeners();
    }

}
