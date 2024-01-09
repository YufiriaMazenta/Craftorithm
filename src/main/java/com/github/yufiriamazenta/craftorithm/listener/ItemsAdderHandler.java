package com.github.yufiriamazenta.craftorithm.listener;

import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import crypticlib.util.ReflectUtil;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum ItemsAdderHandler implements Listener {

    INSTANCE;
    private final List<RegisteredListener> prepareCraftIAListeners = new ArrayList<>();
    private final List<RegisteredListener> prepareSmithingIAListeners = new ArrayList<>();
    private final List<RegisteredListener> smithIAListeners = new ArrayList<>();
    private final List<RegisteredListener> cookingIAListeners = new ArrayList<>();
    private final Plugin ITEMS_ADDER_PLUGIN;
    private final String ITEMS_ADDER_PLUGIN_NAME = "ItemsAdder";
    private final Field executorField = ReflectUtil.getDeclaredField(RegisteredListener.class, "executor");

    ItemsAdderHandler() {
        ITEMS_ADDER_PLUGIN = Bukkit.getPluginManager().getPlugin(ITEMS_ADDER_PLUGIN_NAME);
        if (ITEMS_ADDER_PLUGIN == null)
            throw new IllegalArgumentException("Can not find ItemsAdder plugin instance");
    }

    @EventHandler
    public void onItemsAdderLoaded(ItemsAdderLoadDataEvent event) {
        RecipeManager.INSTANCE.reloadRecipeManager();
        //注销IA合成监听器
        prepareCraftIAListeners.clear();
        for (RegisteredListener registeredListener : PrepareItemCraftEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(ITEMS_ADDER_PLUGIN_NAME)) {
                prepareCraftIAListeners.add(registeredListener);
            }
        }
        PrepareItemCraftEvent.getHandlerList().unregister(ITEMS_ADDER_PLUGIN);

        //注销IA锻造监听器
        prepareSmithingIAListeners.clear();
        for (RegisteredListener registeredListener : PrepareSmithingEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(ITEMS_ADDER_PLUGIN_NAME)) {
                prepareSmithingIAListeners.add(registeredListener);
            }
        }
        PrepareSmithingEvent.getHandlerList().unregister(ITEMS_ADDER_PLUGIN);

        smithIAListeners.clear();
        for (RegisteredListener registeredListener : SmithItemEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(ITEMS_ADDER_PLUGIN_NAME)) {
                smithIAListeners.add(registeredListener);
            }
        }
        SmithItemEvent.getHandlerList().unregister(ITEMS_ADDER_PLUGIN);

        //注销IA烧炼监听器
        cookingIAListeners.clear();
        for (RegisteredListener registeredListener : FurnaceSmeltEvent.getHandlerList().getRegisteredListeners()) {
            if (registeredListener.getPlugin().getName().equals(ITEMS_ADDER_PLUGIN_NAME)) {
                cookingIAListeners.add(registeredListener);
            }
        }
        FurnaceSmeltEvent.getHandlerList().unregister(ITEMS_ADDER_PLUGIN);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyIAPrepareCraft(PrepareItemCraftEvent event) {
        if (prepareCraftIAListeners.isEmpty())
            return;
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            executeIAPrepareCraftItemListener(event);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeIAPrepareCraftItemListener(event);
        }
    }

    public void executeIAPrepareCraftItemListener(PrepareItemCraftEvent event) {
        for (RegisteredListener prepareCraftIAListener : prepareCraftIAListeners) {
            try {
                getRegisteredListenerExecutor(prepareCraftIAListener).execute(prepareCraftIAListener.getListener(), event);
            } catch (EventException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyIAPrepareSmithing(PrepareSmithingEvent event) {
        if (prepareSmithingIAListeners.isEmpty())
            return;
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null) {
            executeIAPrepareSmithingListener(event);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeIAPrepareSmithingListener(event);
        }
    }

    public void executeIAPrepareSmithingListener(PrepareSmithingEvent event) {
        for (RegisteredListener prepareSmithingIAListener : prepareSmithingIAListeners) {
            try {
                getRegisteredListenerExecutor(prepareSmithingIAListener).execute(prepareSmithingIAListener.getListener(), event);
            } catch (EventException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyIASmithing(SmithItemEvent event) {
        if (prepareSmithingIAListeners.isEmpty())
            return;
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null) {
            executeIASmithItemListener(event);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeIASmithItemListener(event);
        }
    }

    public void executeIASmithItemListener(SmithItemEvent event) {
        for (RegisteredListener smithIAListener : smithIAListeners) {
            try {
                getRegisteredListenerExecutor(smithIAListener).execute(smithIAListener.getListener(), event);
            } catch (EventException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void proxyIACooking(FurnaceSmeltEvent event) {
        if (cookingIAListeners.isEmpty())
            return;
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            executeIACookingListener(event);
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null || !recipeKey.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
            executeIACookingListener(event);
        }
    }

    public void executeIACookingListener(FurnaceSmeltEvent event) {
        for (RegisteredListener cookingIAListener : cookingIAListeners) {
            try {
                getRegisteredListenerExecutor(cookingIAListener).execute(cookingIAListener.getListener(), event);
            } catch (EventException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public EventExecutor getRegisteredListenerExecutor(RegisteredListener registeredListener) {
        return (EventExecutor) ReflectUtil.getDeclaredFieldObj(executorField, registeredListener);
    }

}
