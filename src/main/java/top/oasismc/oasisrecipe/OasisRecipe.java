package top.oasismc.oasisrecipe;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import top.oasismc.oasisrecipe.bstat.Metrics;
import top.oasismc.oasisrecipe.cmd.PluginCommand;
import top.oasismc.oasisrecipe.cmd.subcmd.ReloadCommand;
import top.oasismc.oasisrecipe.config.ConfigUpdater;
import top.oasismc.oasisrecipe.item.ItemLoader;
import top.oasismc.oasisrecipe.listener.FurnaceSmeltListener;
import top.oasismc.oasisrecipe.listener.CraftRecipeListener;
import top.oasismc.oasisrecipe.listener.SmithingListener;
import top.oasismc.oasisrecipe.script.action.ActionDispatcher;
import top.oasismc.oasisrecipe.script.condition.ConditionDispatcher;
import top.oasismc.oasisrecipe.update.UpdateUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class OasisRecipe extends JavaPlugin implements Listener {

    private static OasisRecipe INSTANCE;
    private int vanillaVersion;
    private ActionDispatcher actionDispatcher;
    private ConditionDispatcher conditionDispatcher;
    private Pattern colorPattern;

    public OasisRecipe() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        INSTANCE.colorPattern = Pattern.compile("&#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
        loadVanillaVersion();
        loadBStat();
        saveDefaultConfig();
        loadConfigs();
        loadCommands();
        loadScripts();
        hookItemsAdder();
        loadListener();
        info("&aLoad 1." + vanillaVersion + " Adapter");
        info(getConfig().getString("messages.load.finish", "messages.load.finish"));
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
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null)
            Bukkit.getPluginManager().registerEvents(ItemLoader.INSTANCE, this);
    }

    private void loadConfigs() {
        ConfigUpdater.INSTANCE.updateConfig();
        ReloadCommand.reloadPlugin();
    }

    private void hookItemsAdder() {
        String messageKey;
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            messageKey = "messages.load.itemsAdderSuccess";
        } else {
            messageKey = "messages.load.itemsAdderFailed";
        }
        OasisRecipe.info(color(OasisRecipe.getInstance().getConfig().getString(messageKey, messageKey)));
    }

    private void loadScripts() {
        actionDispatcher = ActionDispatcher.INSTANCE;
        conditionDispatcher = ConditionDispatcher.INSTANCE;
    }

    public void sendMsg(CommandSender sender, String key) {
        if (sender == null) {
            return;
        }
        key = "messages." + key;
        String message = getConfig().getString(key, key);
        message = message.replace("%player%", sender.getName());
        message = message.replace("%version%", getDescription().getVersion());
        sender.sendMessage(color(message));
    }

    public static OasisRecipe getInstance() {
        return INSTANCE;
    }

    public static String color(String text) {
        if (getInstance().vanillaVersion >= 16) {
            StringBuilder strBuilder = new StringBuilder(text);
            Matcher matcher = getInstance().colorPattern.matcher(strBuilder);
            while (matcher.find()) {
                String colorCode = matcher.group();
                String colorStr = ChatColor.of(colorCode.substring(1)).toString();
                strBuilder.replace(matcher.start(), matcher.start() + colorCode.length(), colorStr);
                matcher = getInstance().colorPattern.matcher(strBuilder);
            }
            text = strBuilder.toString();
        }
        return translateAlternateColorCodes('&', text);
    }

    public static void info(String text) {
        Bukkit.getConsoleSender().sendMessage(color("&8[&3Oasis&bRecipe&8] &bINFO &8| &r" + text));
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
