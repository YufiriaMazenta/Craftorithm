package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.CrypticLib;
import crypticlib.platform.IPlatform;
import crypticlib.util.ReflectUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum OtherPluginsListenerProxy implements Listener {

    INSTANCE;
    private final Field executorField = ReflectUtil.getDeclaredField(RegisteredListener.class, "executor");
    private final Map<EventPriority, List<RegisteredListener>> prepareItemCraftEventListeners = new ConcurrentHashMap<>();
    //因为CraftItemEvent与SmithItemEvent的handler list与InventoryClickEvent共享,所以只能放在一起
    private final Map<EventPriority, List<RegisteredListener>> inventoryClickEventListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> prepareSmithingItemEventListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> furnaceSmeltListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> blockCookListeners = new ConcurrentHashMap<>();

    public void reloadOtherPluginsListener() {
        loadProxyPrepareItemCraftEventListeners();
        loadProxyInventoryClickEventListeners();
        loadProxyPrepareSmithingItemEventListeners();
        loadProxyFurnaceSmeltListeners();
        loadBlockCookListeners();
    }

    private void loadBlockCookListeners() {
        for (RegisteredListener registeredListener : BlockCookEvent.getHandlerList().getRegisteredListeners()) {
            if (addListenerCache(registeredListener, blockCookListeners)) {
                BlockCookEvent.getHandlerList().unregister(registeredListener);
            }
        }
    }

    private void loadProxyPrepareItemCraftEventListeners() {
        for (RegisteredListener registeredListener : PrepareItemCraftEvent.getHandlerList().getRegisteredListeners()) {
            if (addListenerCache(registeredListener, prepareItemCraftEventListeners)) {
                PrepareItemCraftEvent.getHandlerList().unregister(registeredListener);
            }
        }
    }

    private void loadProxyInventoryClickEventListeners() {
        for (RegisteredListener registeredListener : InventoryClickEvent.getHandlerList().getRegisteredListeners()) {
            if (addListenerCache(registeredListener, inventoryClickEventListeners)) {
                InventoryClickEvent.getHandlerList().unregister(registeredListener);
            }
        }
    }

    private void loadProxyPrepareSmithingItemEventListeners() {
        for (RegisteredListener registeredListener : PrepareSmithingEvent.getHandlerList().getRegisteredListeners()) {
            if (addListenerCache(registeredListener, prepareSmithingItemEventListeners)) {
                PrepareSmithingEvent.getHandlerList().unregister(registeredListener);
            }
        }
    }

    private void loadProxyFurnaceSmeltListeners() {
        for (RegisteredListener registeredListener : FurnaceSmeltEvent.getHandlerList().getRegisteredListeners()) {
            if (addListenerCache(registeredListener, furnaceSmeltListeners)) {
                FurnaceSmeltEvent.getHandlerList().unregister(registeredListener);
            }
        }
    }

    private boolean addListenerCache(RegisteredListener registeredListener, Map<EventPriority, List<RegisteredListener>> listenerMap) {
        if (registeredListener.getPlugin().getName().equals("Craftorithm")) {
            return false;
        }
        EventPriority priority = registeredListener.getPriority();
        if (listenerMap.containsKey(priority)) {
            List<RegisteredListener> listeners = listenerMap.get(priority);
            if (contains(listeners, registeredListener))
                listeners.remove(registeredListener);
            listeners.add(registeredListener);
        } else {
            List<RegisteredListener> listeners = new ArrayList<>();
            listeners.add(registeredListener);
            listenerMap.put(priority, listeners);
        }
        return true;
    }

    public EventExecutor getRegisteredListenerExecutor(RegisteredListener registeredListener) {
        return ReflectUtil.getDeclaredFieldObj(executorField, registeredListener);
    }

    private boolean contains(List<RegisteredListener> registeredListeners, RegisteredListener listener) {
        for (RegisteredListener registeredListener : registeredListeners) {
            if (registeredListener.getListener().getClass().equals(listener.getListener().getClass())) {
                return true;
            }
        }
        return false;
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestPrepareItemCraft(PrepareItemCraftEvent event) {
        proxyPrepareItemCraft(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowPrepareItemCraft(PrepareItemCraftEvent event) {
        proxyPrepareItemCraft(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalPrepareItemCraft(PrepareItemCraftEvent event) {
        proxyPrepareItemCraft(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighPrepareItemCraft(PrepareItemCraftEvent event) {
        proxyPrepareItemCraft(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestPrepareItemCraft(PrepareItemCraftEvent event) {
        proxyPrepareItemCraft(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorPrepareItemCraft(PrepareItemCraftEvent event) {
        proxyPrepareItemCraft(event, EventPriority.MONITOR);
    }

    private void proxyPrepareItemCraft(PrepareItemCraftEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = prepareItemCraftEventListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            executeListener(event, registeredListeners);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeListener(event, registeredListeners);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowCraftItem(CraftItemEvent event) {
        proxyCraftItem(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalCraftItem(CraftItemEvent event) {
        proxyCraftItem(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighCraftItem(CraftItemEvent event) {
        proxyCraftItem(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestCraftItem(CraftItemEvent event) {
        proxyCraftItem(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorCraftItem(CraftItemEvent event) {
        proxyCraftItem(event, EventPriority.MONITOR);
    }

    private void proxyCraftItem(CraftItemEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = inventoryClickEventListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        Recipe recipe = event.getRecipe();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeListener(event, registeredListeners);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestPrepareSmithing(PrepareSmithingEvent event) {
        proxyPrepareSmithing(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowPrepareSmithing(PrepareSmithingEvent event) {
        proxyPrepareSmithing(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalPrepareSmithing(PrepareSmithingEvent event) {
        proxyPrepareSmithing(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighPrepareSmithing(PrepareSmithingEvent event) {
        proxyPrepareSmithing(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestPrepareSmithing(PrepareSmithingEvent event) {
        proxyPrepareSmithing(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorPrepareSmithing(PrepareSmithingEvent event) {
        proxyPrepareSmithing(event, EventPriority.MONITOR);
    }

    private void proxyPrepareSmithing(PrepareSmithingEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = prepareSmithingItemEventListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null) {
            executeListener(event, registeredListeners);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeListener(event, registeredListeners);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestPrepareAnvil(PrepareAnvilEvent event) {
        proxyPrepareAnvil(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowPrepareAnvil(PrepareAnvilEvent event) {
        proxyPrepareAnvil(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalPrepareAnvil(PrepareAnvilEvent event) {
        proxyPrepareAnvil(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighPrepareAnvil(PrepareAnvilEvent event) {
        proxyPrepareAnvil(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestPrepareAnvil(PrepareAnvilEvent event) {
        proxyPrepareAnvil(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorPrepareAnvil(PrepareAnvilEvent event) {
        proxyPrepareAnvil(event, EventPriority.MONITOR);
    }

    private void proxyPrepareAnvil(PrepareAnvilEvent event, EventPriority eventPriority) {
        //因为只有paper及下游服务端才有这个问题,如果识别到是bukkit或者spigot,就不用处理
        if (CrypticLib.platform().platform().equals(IPlatform.Platform.BUKKIT)) {
            return;
        }
        List<RegisteredListener> registeredListeners = prepareSmithingItemEventListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        executeListener(event, registeredListeners);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestSmithItem(SmithItemEvent event) {
        proxySmithItem(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowSmithItem(SmithItemEvent event) {
        proxySmithItem(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalSmithItem(SmithItemEvent event) {
        proxySmithItem(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighSmithItem(SmithItemEvent event) {
        proxySmithItem(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestSmithItem(SmithItemEvent event) {
        proxySmithItem(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorSmithItem(SmithItemEvent event) {
        proxySmithItem(event, EventPriority.MONITOR);
    }

    private void proxySmithItem(SmithItemEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = inventoryClickEventListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null) {
            executeListener(event, registeredListeners);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeListener(event, registeredListeners);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestFurnaceSmelt(FurnaceSmeltEvent event) {
        proxyFurnaceSmelt(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowFurnaceSmelt(FurnaceSmeltEvent event) {
        proxyFurnaceSmelt(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalFurnaceSmelt(FurnaceSmeltEvent event) {
        proxyFurnaceSmelt(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighFurnaceSmelt(FurnaceSmeltEvent event) {
        proxyFurnaceSmelt(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestFurnaceSmelt(FurnaceSmeltEvent event) {
        proxyFurnaceSmelt(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorFurnaceSmelt(FurnaceSmeltEvent event) {
        proxyFurnaceSmelt(event, EventPriority.MONITOR);
    }

    private void proxyFurnaceSmelt(FurnaceSmeltEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = furnaceSmeltListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            executeListener(event, registeredListeners);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeListener(event, registeredListeners);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestBlockCook(BlockCookEvent event) {
        proxyBlockCook(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowBlockCook(BlockCookEvent event) {
        proxyBlockCook(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalBlockCook(BlockCookEvent event) {
        proxyBlockCook(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighBlockCook(BlockCookEvent event) {
        proxyBlockCook(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestBlockCook(BlockCookEvent event) {
        proxyBlockCook(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorBlockCook(BlockCookEvent event) {
        proxyBlockCook(event, EventPriority.MONITOR);
    }

    private void proxyBlockCook(BlockCookEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = blockCookListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            executeListener(event, registeredListeners);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeListener(event, registeredListeners);
        }
    }

    //因为CraftItemEvent与SmithItemEvent的handler list与InventoryClickEvent共享,所以必须也代理此事件
    //否则将会影响其他插件操作页面
    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestInventoryClick(InventoryClickEvent event) {
        proxyInventoryClick(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowInventoryClick(InventoryClickEvent event) {
        proxyInventoryClick(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalInventoryClick(InventoryClickEvent event) {
        proxyInventoryClick(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighInventoryClick(InventoryClickEvent event) {
        proxyInventoryClick(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestInventoryClick(InventoryClickEvent event) {
        proxyInventoryClick(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorInventoryClick(InventoryClickEvent event) {
        proxyInventoryClick(event, EventPriority.MONITOR);
    }

    private void proxyInventoryClick(InventoryClickEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = inventoryClickEventListeners.get(eventPriority);
        if (registeredListeners == null) {
            return;
        }
        if (registeredListeners.isEmpty()) {
            return;
        }
        executeListener(event, registeredListeners);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void proxyLowestPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.HIGHEST);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorPrepareGrindstone(PrepareGrindstoneEvent event) {
        proxyPrepareGrindstone(event, EventPriority.MONITOR);
    }

    public void proxyPrepareGrindstone(PrepareGrindstoneEvent event, EventPriority priority) {
        //因为只有paper及下游服务端才有这个问题,如果识别到是bukkit或者spigot,就不用处理
        if (CrypticLib.platform().platform().equals(IPlatform.Platform.BUKKIT)) {
            return;
        }
        List<RegisteredListener> registeredListeners = prepareItemCraftEventListeners.get(priority);
        if (registeredListeners == null || registeredListeners.isEmpty()) {
            return;
        }
        executeListener(event, registeredListeners);
    }

    public void executeListener(Event event, List<RegisteredListener> registeredListeners) {
        for (RegisteredListener prepareCraftIAListener : registeredListeners) {
            try {
                getRegisteredListenerExecutor(prepareCraftIAListener).execute(prepareCraftIAListener.getListener(), event);
            } catch (Throwable e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public Map<EventPriority, List<RegisteredListener>> getPrepareItemCraftEventListeners() {
        return prepareItemCraftEventListeners;
    }

    public Map<EventPriority, List<RegisteredListener>> getInventoryClickEventListeners() {
        return inventoryClickEventListeners;
    }

    public Map<EventPriority, List<RegisteredListener>> getPrepareSmithingItemEventListeners() {
        return prepareSmithingItemEventListeners;
    }

    public Map<EventPriority, List<RegisteredListener>> getFurnaceSmeltListeners() {
        return furnaceSmeltListeners;
    }

    public Map<EventPriority, List<RegisteredListener>> getBlockCookListeners() {
        return blockCookListeners;
    }
}
