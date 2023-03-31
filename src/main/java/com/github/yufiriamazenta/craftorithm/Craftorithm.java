package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.cmd.PluginCommand;
import com.github.yufiriamazenta.craftorithm.config.ConfigUpdater;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.listener.*;
import com.github.yufiriamazenta.craftorithm.menu.bukkit.BukkitMenuDispatcher;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import com.github.yufiriamazenta.craftorithm.util.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Craftorithm extends JavaPlugin implements Listener {

    private static Craftorithm INSTANCE;
    private int vanillaVersion;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        loadVanillaVersion();
        saveDefaultConfig();
        ConfigUpdater.INSTANCE.updateConfig();

        ItemManager.loadItemManager();
        RecipeManager.loadRecipeManager();
        regCommands();
        regListeners();
        PluginHookUtil.hookPlugins();
        initArcenciel();
        loadBStat();
        
        LangUtil.info("load.finish");
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
    }

    @Override
    public void onDisable() {
        RecipeManager.resetRecipes();
    }

    private void loadVanillaVersion() {
        String versionStr = Bukkit.getBukkitVersion();
        int index1 = versionStr.indexOf(".");
        int index2 = versionStr.indexOf(".", index1 + 1);
        versionStr = versionStr.substring(index1 + 1, index2);
        try {
            vanillaVersion = Integer.parseInt(versionStr);
        } catch (NumberFormatException e) {
            vanillaVersion = Integer.parseInt(versionStr.substring(0, versionStr.indexOf("-")));
        }
    }

    private void loadBStat() {
        Metrics metrics = new Metrics(this, 17821);
        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.getRecipeFileMap().keySet().size()));
    }

    private void regCommands() {
        Bukkit.getPluginCommand("craftorithm").setExecutor(PluginCommand.INSTANCE);
        Bukkit.getPluginCommand("craftorithm").setTabCompleter(PluginCommand.INSTANCE);
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(CraftHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(RecipeUnlockHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(AnvilRecipeHandler.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(BukkitMenuDispatcher.INSTANCE, this);
        if (getVanillaVersion() >= 14)
            Bukkit.getPluginManager().registerEvents(SmithingHandler.INSTANCE, this);
        if (getVanillaVersion() >= 17)
            Bukkit.getPluginManager().registerEvents(FurnaceSmeltHandler.INSTANCE, this);
    }

    private void initArcenciel() {
        ArcencielDispatcher.INSTANCE.loadFuncFile();
    }

    public static Craftorithm getInstance() {
        return INSTANCE;
    }

    public int getVanillaVersion() {
        return vanillaVersion;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.checkUpdate(event.getPlayer());
        }
    }

}
