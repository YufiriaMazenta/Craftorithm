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
    //鐢ㄤ簬璁板綍涓€涓狶istener绫昏杞崲浜嗗灏戞
    private final Map<String, Integer> listenerConvertedCountMap = new ConcurrentHashMap<>();
    //鐢ㄤ簬璁板綍涓€涓狶istener绫诲湪閲嶆柊娉ㄥ唽鏃跺彲浠ユ敞鍐屽嚑涓�,涓昏鏄敤浜庝竴浜涙儏鍐典笅鍏朵粬鎻掍欢浼氫富鍔ㄩ噸鏂版敞鍐岃嚜宸辩殑鐩戝惉鍣�
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
                    //濡傛灉璇ョ洃鍚櫒琚厤缃负涓嶈浆鍖�,鍒欑洿鎺ヨ烦杩�
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
                //宸茬粡涓嶅啀鍏佽娉ㄥ唽杩欎釜鐩戝惉绫荤殑鐩戝惉鍣ㄤ簡
                continue;
            }
            int convertedNum = listenerConvertedCountMap.getOrDefault(convertedListenerName, 0);
            if (allowReregisterNum == convertedNum) {
                //濡傛灉鍏佽閲嶆柊娉ㄥ唽鐨勬暟閲忎笌涔嬪墠杞寲鐨勬暟閲忕浉绛�,鎰忓懗鐫€杩欐槸绗竴娆′负杩欎釜鐩戝惉鍣ㄧ被娉ㄥ唽
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
            //璺宠繃RecipeCheck鍖呰鍣�,閬垮厤灏嗗皻鏈繕鍘熺殑鍚屽悕鐩戝惉鍣ㄨ鍒や负"鍏朵粬鎻掍欢宸查噸鏂版敞鍐�"
            if (registeredListener instanceof RecipeCheckRegisteredListener || registeredListener instanceof RecipeCheckTimedRegisteredListener) {
                continue;
            }
            if (Objects.equals(
                registeredListener.getListener().getClass(),
                convertedRegisteredListener.originRegisteredListener().getListener().getClass()
            )) {
                //濡傛灉鍦℉andlerList閲屽凡缁忓瓨鍦ㄤ簡杩欎釜绫荤殑鐩戝惉鍣�,鎰忓懗鐫€鍏朵粬鎻掍欢宸茬粡鑷繁閲嶆柊娉ㄥ唽浜�,杩欐椂鍊欐垜浠氨涓嶅簲璇ラ噸鏂版敞鍐�
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
