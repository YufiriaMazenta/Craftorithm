package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.config.ConfigUpdater;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.listener.*;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuDispatcher;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import com.github.yufiriamazenta.craftorithm.util.UpdateUtil;
import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Craftorithm extends BukkitPlugin implements Listener {

    private static Craftorithm INSTANCE;
    private boolean hasLoadPluginRecipeMap = false;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        saveDefaultConfig();
        ConfigUpdater.INSTANCE.updateConfig();

        ItemManager.loadItemManager();
        RecipeManager.loadRecipeManager();
        regListeners();
        PluginHookUtil.hookPlugins();
        initArcenciel();
        loadBStat();
        
        LangUtil.info("load.finish");
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
    }

    @Override
    public void disable() {
        RecipeManager.resetRecipes();
    }

    private void loadBStat() {
        Metrics metrics = new Metrics(this, 17821);
        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.getRecipeFileMap().keySet().size()));
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(CraftHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(RecipeUnlockHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(AnvilRecipeHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(BukkitMenuDispatcher.INSTANCE, this);
        if (CrypticLib.minecraftVersion() >= 11400)
            Bukkit.getPluginManager().registerEvents(SmithingHandler.INSTANCE, this);
        if (CrypticLib.minecraftVersion() >= 11700)
            Bukkit.getPluginManager().registerEvents(FurnaceSmeltHandler.INSTANCE, this);
    }

    private void initArcenciel() {
        ArcencielDispatcher.INSTANCE.loadFuncFile();
    }

    public static Craftorithm getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.checkUpdate(event.getPlayer());
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (!hasLoadPluginRecipeMap) {
            hasLoadPluginRecipeMap = true;
            Map<String, List<Recipe>> map = CraftorithmAPI.INSTANCE.getPluginRegRecipeMap();
            Iterator<Recipe> iterator = Bukkit.getServer().recipeIterator();
            while (iterator.hasNext()) {
                Recipe recipe = iterator.next();
                NamespacedKey key = RecipeManager.getRecipeKey(recipe);
                String namespace = key.getNamespace();
                if (map.containsKey(namespace)) {
                    map.get(namespace).add(recipe);
                } else {
                    List<Recipe> recipes = new ArrayList<>();
                    recipes.add(recipe);
                    map.put(namespace, recipes);
                }
            }
            RecipeManager.loadRecipes();
        }
    }
}
