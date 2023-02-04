package top.oasismc.oasisrecipe;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import top.oasismc.oasisrecipe.bstat.Metrics;
import top.oasismc.oasisrecipe.cmd.PluginCommand;
import top.oasismc.oasisrecipe.cmd.subcmd.ReloadCommand;
import top.oasismc.oasisrecipe.config.ConfigUpdater;
import top.oasismc.oasisrecipe.item.ItemManager;
import top.oasismc.oasisrecipe.listener.CraftRecipeListener;
import top.oasismc.oasisrecipe.listener.FurnaceSmeltListener;
import top.oasismc.oasisrecipe.listener.SmithingListener;
import top.oasismc.oasisrecipe.recipe.RecipeManager;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.condition.ConditionDispatcher;
import top.oasismc.oasisrecipe.util.MsgUtil;
import top.oasismc.oasisrecipe.util.UpdateUtil;

public final class OasisRecipe extends JavaPlugin implements Listener {

    private static OasisRecipe INSTANCE;
    private int vanillaVersion;
    private ActionDispatcher actionDispatcher;
    private ConditionDispatcher conditionDispatcher;

    public OasisRecipe() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        ItemManager.loadItems();
        RecipeManager.loadRecipes();
        loadVanillaVersion();
        saveDefaultConfig();
        loadConfigs();
        loadCommands();
        loadScripts();
        loadListener();
        MsgUtil.info("&aLoad 1." + vanillaVersion + " Adapter");
        MsgUtil.info(getConfig().getString("messages.load.finish", "messages.load.finish"));
        UpdateUtil.checkUpdate(Bukkit.getConsoleSender());
        loadBStat();
    }

    @Override
    public void onDisable() {
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
        new Metrics(this, 15016);
    }

    private void loadCommands() {
        Bukkit.getPluginCommand("oasisrecipe").setExecutor(PluginCommand.INSTANCE);
        Bukkit.getPluginCommand("oasisrecipe").setTabCompleter(PluginCommand.INSTANCE);
    }

    private void loadListener() {
        Bukkit.getPluginManager().registerEvents(CraftRecipeListener.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        if (getVanillaVersion() >= 14)
            Bukkit.getPluginManager().registerEvents(SmithingListener.INSTANCE, this);
        if (getVanillaVersion() >= 18)
            Bukkit.getPluginManager().registerEvents(FurnaceSmeltListener.INSTANCE, this);
    }

    private void loadConfigs() {
        ConfigUpdater.INSTANCE.updateConfig();
        ReloadCommand.reloadPlugin();
    }

    private void loadScripts() {
        actionDispatcher = ActionDispatcher.INSTANCE;
        conditionDispatcher = ConditionDispatcher.INSTANCE;
    }

    public static OasisRecipe getInstance() {
        return INSTANCE;
    }

    public int getVanillaVersion() {
        return vanillaVersion;
    }

    public ActionDispatcher getActionDispatcher() {
        return actionDispatcher;
    }

    public ConditionDispatcher getConditionDispatcher() {
        return conditionDispatcher;
    }

    public void setActionDispatcher(ActionDispatcher actionDispatcher) {
        this.actionDispatcher = actionDispatcher;
    }

    public void setConditionDispatcher(ConditionDispatcher conditionDispatcher) {
        this.conditionDispatcher = conditionDispatcher;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateUtil.checkUpdate(event.getPlayer());
        }
    }

}
