package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.listener.BukkitListener;
import crypticlib.util.ReflectUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@BukkitListener
public enum OtherPluginsListenerHandler implements Listener {

    INSTANCE;
    private final Field executorField = ReflectUtil.getDeclaredField(RegisteredListener.class, "executor");
    private final Map<EventPriority, List<RegisteredListener>> prepareItemCraftEventListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> craftItemEventListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> prePareSmithingItemEventListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> smithingItemEventListeners = new ConcurrentHashMap<>();
    private final Map<EventPriority, List<RegisteredListener>> furnaceSmeltListeners = new ConcurrentHashMap<>();

    public void reloadOtherPluginsListener() {
        loadProxyPrepareItemCraftEventListeners();
        loadProxyCraftItemEventListeners();
        loadProxyPrepareSmithingItemEventListeners();
        loadProxySmithingItemEventListeners();
        loadProxyFurnaceSmeltListeners();
    }

    private void loadProxyPrepareItemCraftEventListeners() {
        for (RegisteredListener registeredListener : PrepareItemCraftEvent.getHandlerList().getRegisteredListeners()) {
            addListenerCache(registeredListener, prepareItemCraftEventListeners);
            PrepareItemCraftEvent.getHandlerList().unregister(registeredListener);
        }
    }

    private void loadProxyCraftItemEventListeners() {
        for (RegisteredListener registeredListener : CraftItemEvent.getHandlerList().getRegisteredListeners()) {
            addListenerCache(registeredListener, craftItemEventListeners);
            CraftItemEvent.getHandlerList().unregister(registeredListener);
        }
    }

    private void loadProxyPrepareSmithingItemEventListeners() {
        for (RegisteredListener registeredListener : PrepareSmithingEvent.getHandlerList().getRegisteredListeners()) {
            addListenerCache(registeredListener, prePareSmithingItemEventListeners);
            PrepareSmithingEvent.getHandlerList().unregister(registeredListener);
        }
    }

    private void loadProxySmithingItemEventListeners() {
        for (RegisteredListener registeredListener : SmithItemEvent.getHandlerList().getRegisteredListeners()) {
            addListenerCache(registeredListener, smithingItemEventListeners);
            SmithItemEvent.getHandlerList().unregister(registeredListener);
        }
    }

    private void loadProxyFurnaceSmeltListeners() {
        for (RegisteredListener registeredListener : FurnaceSmeltEvent.getHandlerList().getRegisteredListeners()) {
            addListenerCache(registeredListener, furnaceSmeltListeners);
            FurnaceSmeltEvent.getHandlerList().unregister(registeredListener);
        }
    }

    private void addListenerCache(RegisteredListener registeredListener, Map<EventPriority, List<RegisteredListener>> listenerMap) {
        if (registeredListener.getPlugin().getName().equals("Craftorithm")) {
            return;
        }
        EventPriority priority = registeredListener.getPriority();
        if (listenerMap.containsKey(priority)) {
            List<RegisteredListener> listeners = listenerMap.get(priority);
            if (contains(listeners, registeredListener.getClass()))
                listeners.remove(registeredListener);
            listeners.add(registeredListener);
        } else {
            List<RegisteredListener> listeners = new ArrayList<>();
            listeners.add(registeredListener);
            listenerMap.put(priority, listeners);
        }
    }

    public EventExecutor getRegisteredListenerExecutor(RegisteredListener registeredListener) {
        return ReflectUtil.getDeclaredFieldObj(executorField, registeredListener);
    }

    private boolean contains(List<RegisteredListener> registeredListeners, Class<?> listenerClass) {
        for (RegisteredListener registeredListener : registeredListeners) {
            if (registeredListener.getListener().getClass().equals(listenerClass)) {
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
        List<RegisteredListener> registeredListeners = craftItemEventListeners.get(eventPriority);
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
        List<RegisteredListener> registeredListeners = prePareSmithingItemEventListeners.get(eventPriority);
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
    public void proxyLowestSmithingItem(SmithItemEvent event) {
        proxySmithingItem(event, EventPriority.LOWEST);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void proxyLowSmithingItem(SmithItemEvent event) {
        proxySmithingItem(event, EventPriority.LOW);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void proxyNormalSmithingItem(SmithItemEvent event) {
        proxySmithingItem(event, EventPriority.NORMAL);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void proxyHighSmithingItem(SmithItemEvent event) {
        proxySmithingItem(event, EventPriority.HIGH);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyHighestSmithingItem(SmithItemEvent event) {
        proxySmithingItem(event, EventPriority.HIGHEST);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyMonitorSmithingItem(SmithItemEvent event) {
        proxySmithingItem(event, EventPriority.MONITOR);
    }

    private void proxySmithingItem(SmithItemEvent event, EventPriority eventPriority) {
        List<RegisteredListener> registeredListeners = smithingItemEventListeners.get(eventPriority);
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

    public void executeListener(Event event, List<RegisteredListener> registeredListeners) {
        for (RegisteredListener prepareCraftIAListener : registeredListeners) {
            try {
                getRegisteredListenerExecutor(prepareCraftIAListener).execute(prepareCraftIAListener.getListener(), event);
            } catch (EventException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
