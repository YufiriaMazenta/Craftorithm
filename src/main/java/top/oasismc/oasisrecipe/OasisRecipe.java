package top.oasismc.oasisrecipe;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import top.oasismc.oasisrecipe.cmd.PluginCommand;
import top.oasismc.oasisrecipe.listener.RecipeCheckListener;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class OasisRecipe extends JavaPlugin {

    private static OasisRecipe PLUGIN;

    public OasisRecipe() {
        PLUGIN = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadCommands();
        loadListener();
        regRecipes();
        info(color(getConfig().getString("messages.load.finish", "messages.load.finish")));
    }

    @Override
    public void onDisable() {
        Bukkit.resetRecipes();
    }

    private void loadCommands() {
        Bukkit.getPluginCommand("oasisrecipe").setExecutor(PluginCommand.getCommand());
        Bukkit.getPluginCommand("oasisrecipe").setTabCompleter(PluginCommand.getCommand());
    }

    private void loadListener() {
        Bukkit.getPluginManager().registerEvents(RecipeCheckListener.getListener(), this);
    }

    private void regRecipes() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Class.forName("top.oasismc.oasisrecipe.recipe.RecipeManager");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(this, 60);
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

    public static OasisRecipe getPlugin() {
        return PLUGIN;
    }

    public static String color(String text) {
        return translateAlternateColorCodes('&', text);
    }

    public static void info(String text) {
        Bukkit.getLogger().info("[OasisRecipe] INFO | " + text);
    }

}
