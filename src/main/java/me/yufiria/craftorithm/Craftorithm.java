package me.yufiria.craftorithm;

import me.yufiria.craftorithm.arcenciel.ArcencielDispatcher;
import me.yufiria.craftorithm.bstat.Metrics;
import me.yufiria.craftorithm.cmd.PluginCommand;
import me.yufiria.craftorithm.config.ConfigUpdater;
import me.yufiria.craftorithm.item.ItemManager;
import me.yufiria.craftorithm.listener.CraftHandler;
import me.yufiria.craftorithm.listener.FurnaceSmeltHandler;
import me.yufiria.craftorithm.listener.RecipeUnlockHandler;
import me.yufiria.craftorithm.listener.SmithingHandler;
import me.yufiria.craftorithm.recipe.RecipeManager;
import me.yufiria.craftorithm.util.LangUtil;
import me.yufiria.craftorithm.util.PluginHookUtil;
import me.yufiria.craftorithm.util.UpdateUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;

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
        
        LangUtil.info("load.finish");
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
        loadBStat();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        Bukkit.resetRecipes();
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
        if (getVanillaVersion() >= 14)
            Bukkit.getPluginManager().registerEvents(SmithingHandler.INSTANCE, this);
        if (getVanillaVersion() >= 18)
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
