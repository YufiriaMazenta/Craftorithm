package pers.yufiria.craftorithm.hook.listener;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
        @TaskRule(lifeCycle = LifeCycle.DISABLE)
    }
)
public enum OtherPluginsListenerManager implements BukkitLifeCycleTask {

    INSTANCE;
    private final Field executorField = ReflectionHelper.getDeclaredField(RegisteredListener.class, "executor");
    private final Map<String, ConvertedRegisteredListener> convertedListenerMap = new ConcurrentHashMap<>();

    private void convertOtherPluginsListeners() {
        for (HandlerList handlerList : getCraftEventHandlerLists()) {
            for (RegisteredListener originRegisteredListener : handlerList.getRegisteredListeners()) {
                if (originRegisteredListener.getPlugin().equals(Craftorithm.instance())) continue;
                if (originRegisteredListener instanceof RecipeCheckRegisteredListener || originRegisteredListener instanceof RecipeCheckTimedRegisteredListener)
                    continue;

                Listener listener = originRegisteredListener.getListener();
                String listenerClassName = listener.getClass().getName();
                if (PluginConfigs.NOT_CONVERT_LISTENER_CLASSES.value().contains(listenerClassName)) {
                    //如果该监听器被配置为不转化,则直接跳过
                    continue;
                }

                RegisteredListener convertedRegisteredListener;
                try {
                    if (originRegisteredListener instanceof TimedRegisteredListener) {
                        convertedRegisteredListener = new RecipeCheckTimedRegisteredListener(
                            originRegisteredListener.getListener(),
                            getRegisteredListenerExecutor(originRegisteredListener),
                            originRegisteredListener.getPriority(),
                            originRegisteredListener.getPlugin(),
                            originRegisteredListener.isIgnoringCancelled()
                        );
                    } else {
                        convertedRegisteredListener = new RecipeCheckRegisteredListener(
                            originRegisteredListener.getListener(),
                            getRegisteredListenerExecutor(originRegisteredListener),
                            originRegisteredListener.getPriority(),
                            originRegisteredListener.getPlugin(),
                            originRegisteredListener.isIgnoringCancelled()
                        );
                    }
                } catch (Throwable ignore) {
                    convertedRegisteredListener = new RecipeCheckRegisteredListener(
                        originRegisteredListener.getListener(),
                        getRegisteredListenerExecutor(originRegisteredListener),
                        originRegisteredListener.getPriority(),
                        originRegisteredListener.getPlugin(),
                        originRegisteredListener.isIgnoringCancelled()
                    );
                }
                handlerList.unregister(originRegisteredListener);
                handlerList.register(convertedRegisteredListener);
                convertedListenerMap.put(listenerClassName, new ConvertedRegisteredListener(
                    listenerClassName,
                    originRegisteredListener,
                    convertedRegisteredListener,
                    handlerList
                ));

                IOHelper.info("Converted listener: " + listenerClassName);
            }
        }
    }

    private void resetRegisteredListeners() {
        convertedListenerMap.forEach((listenerClassName, convertedRegisteredListener) -> {
            HandlerList handlerList = convertedRegisteredListener.handlerList();
            handlerList.unregister(convertedRegisteredListener.recipeCheckRegisteredListener());
            handlerList.register(convertedRegisteredListener.originRegisteredListener());
        });
        convertedListenerMap.clear();
    }

    private EventExecutor getRegisteredListenerExecutor(RegisteredListener registeredListener) {
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
        switch (lifeCycle) {
            case ACTIVE -> {
                convertOtherPluginsListeners();
            }
            case RELOAD -> {
                resetRegisteredListeners();
                convertOtherPluginsListeners();
            }
            case DISABLE -> {
                resetRegisteredListeners();
            }
        }
    }

}
