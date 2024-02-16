package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.util.ReflectUtil;
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum OraxenHandler implements Listener {

    INSTANCE;
    private final List<RegisteredListener> prepareCraftOraxenListeners = new ArrayList<>();
    private final Plugin ORAXEN_PLUGIN;
    private final String ORAXEN_PLUGIN_NAME = "Oraxen";
    private final Field executorField = ReflectUtil.getDeclaredField(RegisteredListener.class, "executor");

    OraxenHandler() {
        ORAXEN_PLUGIN = Bukkit.getPluginManager().getPlugin(ORAXEN_PLUGIN_NAME);
        if (ORAXEN_PLUGIN == null)
            throw new IllegalArgumentException("Can not find Oraxen plugin instance");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOraxenLoaded(OraxenItemsLoadedEvent event) {
        //注销Oraxen合成监听器
        prepareCraftOraxenListeners.clear();
        for (RegisteredListener registeredListener : PrepareItemCraftEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(ORAXEN_PLUGIN_NAME)) {
                prepareCraftOraxenListeners.add(registeredListener);
            }
        }
        PrepareItemCraftEvent.getHandlerList().unregister(ORAXEN_PLUGIN);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void proxyOraxenPrepareCraft(PrepareItemCraftEvent event) {
        if (prepareCraftOraxenListeners.isEmpty())
            return;
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            executeOraxenPrepareCraftItemListener(event);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeOraxenPrepareCraftItemListener(event);
        }
    }

    public void executeOraxenPrepareCraftItemListener(PrepareItemCraftEvent event) {
        for (RegisteredListener prepareCraftIAListener : prepareCraftOraxenListeners) {
            try {
                getRegisteredListenerExecutor(prepareCraftIAListener).execute(prepareCraftIAListener.getListener(), event);
            } catch (EventException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public EventExecutor getRegisteredListenerExecutor(RegisteredListener registeredListener) {
        return (EventExecutor) ReflectUtil.getDeclaredFieldObj(executorField, registeredListener);
    }

}