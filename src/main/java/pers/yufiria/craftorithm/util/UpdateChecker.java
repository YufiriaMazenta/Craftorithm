package pers.yufiria.craftorithm.util;

import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@EventListener
public class UpdateChecker implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            UpdateChecker.pullUpdateCheckRequest(event.getPlayer());
        }
    }

    public static void pullUpdateCheckRequest(CommandSender sender) {
        if (!PluginConfigs.CHECK_UPDATE.value())
            return;
        CrypticLibBukkit.scheduler().async(() -> {
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=108429/");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                InputStream is = conn.getInputStream();
                String latestVersion = new BufferedReader(new InputStreamReader(is)).readLine();
                String pluginVersion = Craftorithm.instance().getDescription().getVersion();
                pluginVersion = pluginVersion.substring(0, pluginVersion.indexOf("-"));
                if (checkVersion(latestVersion, pluginVersion)) {
                    CrypticLibBukkit.scheduler().sync(() -> {
                        LangUtils.sendLang(sender, Languages.NEW_VERSION, CollectionsUtils.newStringHashMap("<new_version>", latestVersion));
                    });
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean checkVersion(String newVersion, String version) {
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
