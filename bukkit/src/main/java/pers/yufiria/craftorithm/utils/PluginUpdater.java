package pers.yufiria.craftorithm.utils;

import crypticlib.CrypticLibBukkit;
import crypticlib.lifecycle.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.CraftorithmBukkit;
import pers.yufiria.craftorithm.config.Configs;
import pers.yufiria.craftorithm.config.Lang;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ENABLE)
)
public enum PluginUpdater implements BukkitLifeCycleTask {

    INSTANCE;

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        checkUpdate(Bukkit.getConsoleSender());
    }

    public void checkUpdate(CommandSender sender) {
        if (!Configs.checkUpdate.value())
            return;
        CraftorithmBukkit plugin = CraftorithmBukkit.instance();
        CrypticLibBukkit.scheduler().runTaskAsync(plugin, () -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=108429/");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                InputStream is = conn.getInputStream();
                String latestVersion = new BufferedReader(new InputStreamReader(is)).readLine();
                String pluginVersion = plugin.getDescription().getVersion();
                pluginVersion = pluginVersion.substring(0, pluginVersion.indexOf("-"));
                if (checkVersion(latestVersion, pluginVersion)) {
                    Lang.updateNewVersionFound.send(sender, Map.of("<new_version>", latestVersion));
                }
                is.close();
            } catch (Exception e) {
                Lang.updateFindUpdatesFailed.send(sender);
            }
        });
    }

    private boolean checkVersion(String newVersion, String version) {
        if (newVersion.equals(version))
            return false;
        String[] newVersionNum = newVersion.split("\\.");
        String[] versionNum = version.split("\\.");
        int newRootVer = Integer.parseInt(newVersionNum[0]);
        int rootVer = Integer.parseInt(versionNum[0]);
        if (rootVer < newRootVer)
            return true;
        else if (rootVer > newRootVer)
            return false;
        int newSubVer = Integer.parseInt(newVersionNum[1]);
        int subVer = Integer.parseInt(versionNum[1]);
        if (newSubVer > subVer)
            return true;
        else if (subVer > newSubVer)
            return false;
        int newLatestVer = Integer.parseInt(newVersionNum[2]);
        int latestVer = Integer.parseInt(versionNum[2]);
        return newLatestVer > latestVer;
    }

}
