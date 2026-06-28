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
import java.util.Objects;
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
    private final List<ConvertedRegisteredListener> convertedListenerList = new ArrayList<>();
    //用于记录一个Listener类被转换了多少次
    private final Map<String, Integer> listenerConvertedCountMap = new ConcurrentHashMap<>();
    //用于记录一个Listener类在重新注册时可以注册几个,主要是用于一些情况下其他插件会主动重新注册自己的监听器
    private final Map<String, Integer> allowReregisterListenerNumMap = new ConcurrentHashMap<>();

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
                listenerConvertedCountMap.merge(listenerClassName, 1, Integer::sum);
                convertedListenerList.add(new ConvertedRegisteredListener(
                    listenerClassName,
                    originRegisteredListener,
                    convertedRegisteredListener,
                    handlerList
                ));

                IOHelper.info("Converted listener: " + listenerClassName);
            }
        }
        allowReregisterListenerNumMap.putAll(listenerConvertedCountMap);
    }

    private void resetRegisteredListeners() {
        for (ConvertedRegisteredListener convertedRegisteredListener : convertedListenerList) {
            HandlerList handlerList = convertedRegisteredListener.handlerList();
            handlerList.unregister(convertedRegisteredListener.recipeCheckRegisteredListener());
            String convertedListenerName = convertedRegisteredListener.listenerClassName();
            int allowReregisterNum = allowReregisterListenerNumMap.getOrDefault(convertedListenerName, 0);
            if (allowReregisterNum <= 0) {
                //已经不再允许注册这个监听类的监听器了
                continue;
            }
            int convertedNum = listenerConvertedCountMap.getOrDefault(convertedListenerName, 0);
            if (allowReregisterNum == convertedNum) {
                //如果允许重新注册的数量与之前转化的数量相等,意味着这是第一次为这个监听器类注册
                boolean hasSameListenerClass = containsSameListenerClass(convertedRegisteredListener, handlerList);
                if (hasSameListenerClass) {
                    allowReregisterListenerNumMap.remove(convertedListenerName);
                    continue;
                }
            }
            handlerList.register(convertedRegisteredListener.originRegisteredListener());
            allowReregisterListenerNumMap.merge(convertedListenerName, -1, Integer::sum);
        }
        convertedListenerList.clear();
        listenerConvertedCountMap.clear();
        allowReregisterListenerNumMap.clear();
    }

    private static boolean containsSameListenerClass(ConvertedRegisteredListener convertedRegisteredListener, HandlerList handlerList) {
        boolean hasSameListenerClass = false;
        for (RegisteredListener registeredListener : handlerList.getRegisteredListeners()) {
            //跳过RecipeCheck包装器,避免将尚未还原的同名监听器误判为"其他插件已重新注册"
            if (registeredListener instanceof RecipeCheckRegisteredListener || registeredListener instanceof RecipeCheckTimedRegisteredListener) {
                continue;
            }
            if (Objects.equals(
                registeredListener.getListener().getClass(),
                convertedRegisteredListener.originRegisteredListener().getListener().getClass()
            )) {
                //如果在HandlerList里已经存在了这个类的监听器,意味着其他插件已经自己重新注册了,这时候我们就不应该重新注册
                hasSameListenerClass = true;
            }
        }
        return hasSameListenerClass;
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
