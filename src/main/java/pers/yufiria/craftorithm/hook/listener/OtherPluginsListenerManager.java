package pers.yufiria.craftorithm.hook.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.listener.EventListener;
import crypticlib.util.ReflectionHelper;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CampfireStartEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.TimedRegisteredListener;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.util.EventUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        for (HandlerList handlerList : getCraftEventHandlerLists()) {
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

    private List<HandlerList> getCraftEventHandlerLists() {
        List<HandlerList> handlersLists = new ArrayList<>();
        handlersLists.add(CraftItemEvent.getHandlerList());
        handlersLists.add(PrepareItemCraftEvent.getHandlerList());
        handlersLists.add(PrepareSmithingEvent.getHandlerList());
        handlersLists.add(SmithItemEvent.getHandlerList());
        handlersLists.add(FurnaceSmeltEvent.getHandlerList());
        handlersLists.add(BlockCookEvent.getHandlerList());
        if (CrypticLibBukkit.isPaper() && PluginConfigs.ENABLE_ANVIL_RECIPE.value()) {
            handlersLists.add(PrepareAnvilEvent.getHandlerList());
        }
        MinecraftVersion currentVersion = MinecraftVersion.current();
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_17_1)) {
            handlersLists.add(FurnaceStartSmeltEvent.getHandlerList());
        }
        if (currentVersion.afterOrEquals(MinecraftVersion.V1_19_3)) {
            handlersLists.add(CampfireStartEvent.getHandlerList());
        }
        if (EventUtils.hasCrafterCraftEvent) {
            handlersLists.add(CrafterCraftEvent.getHandlerList());
        }
        return handlersLists;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        convertOtherPluginsListeners();
    }

}
