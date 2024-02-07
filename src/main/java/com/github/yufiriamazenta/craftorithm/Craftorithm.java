package com.github.yufiriamazenta.craftorithm;

import com.github.yufiriamazenta.craftorithm.arcenciel.ArcencielDispatcher;
import com.github.yufiriamazenta.craftorithm.bstat.Metrics;
import com.github.yufiriamazenta.craftorithm.config.Languages;
import com.github.yufiriamazenta.craftorithm.config.PluginConfigs;
import com.github.yufiriamazenta.craftorithm.item.ItemManager;
import com.github.yufiriamazenta.craftorithm.listener.ItemsAdderHandler;
import com.github.yufiriamazenta.craftorithm.recipe.RecipeManager;
import com.github.yufiriamazenta.craftorithm.util.LangUtil;
import com.github.yufiriamazenta.craftorithm.util.PluginHookUtil;
import com.github.yufiriamazenta.craftorithm.util.UpdateUtil;
import crypticlib.BukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;

public final class Craftorithm extends BukkitPlugin implements Listener {

    private static Craftorithm INSTANCE;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        ItemManager.INSTANCE.loadItemManager();
        regListeners();
        initArcenciel();
        loadBStat();

        PluginHookUtil.hookPlugins();
        LangUtil.info(Languages.LOAD_FINISH);
    }

    @Override
    public void disable() {
        RecipeManager.INSTANCE.resetRecipes();
    }

    private void loadBStat() {
        if (!PluginConfigs.BSTATS.value())
            return;
        Metrics metrics = new Metrics(this, 17821);
        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.INSTANCE.getRecipeGroups().size()));
    }

    private void regListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void initArcenciel() {
        ArcencielDispatcher.INSTANCE.loadFuncFile();
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    public static CraftorithmAPI api() {
        return CraftorithmAPI.INSTANCE;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.pullUpdateCheckRequest(event.getPlayer());
        }
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (!PluginHookUtil.isItemsAdderLoaded()) {
            RecipeManager.INSTANCE.reloadRecipeManager();
            return;
        }
        Bukkit.getPluginManager().registerEvents(ItemsAdderHandler.INSTANCE, this);
        UpdateUtil.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }
}
