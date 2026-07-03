package pers.yufiria.craftorithm.util;

import crypticlib.CrypticLibBukkit;
import crypticlib.listener.EventListener;
import crypticlib.util.IOHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;

import java.io.BufferedReader;
import java.io.IOException;
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
            InputStream is = null;
            try {
                URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=108429/");
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                is = conn.getInputStream();
                String latestVersion = new BufferedReader(new InputStreamReader(is)).readLine();
                String pluginVersion = Craftorithm.instance().getDescription().getVersion();
                pluginVersion = pluginVersion.substring(0, pluginVersion.indexOf("-"));
                if (checkVersion(latestVersion, pluginVersion)) {
                    CrypticLibBukkit.scheduler().sync(() -> {
                        LangUtils.sendLang(sender, Languages.NEW_VERSION, CollectionsUtils.newStringHashMap("<new_version>", latestVersion));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static boolean checkVersion(String newVersion, String version) {
        if (newVersion.equals(version))
            return false;
        String[] newVersionNum = newVersion.split("\\.");
        String[] versionNum = version.split("\\.");
        int length = Math.max(newVersionNum.length, versionNum.length);
        for (int i = 0; i < length; i++) {
            int newPart = i < newVersionNum.length ? Integer.parseInt(newVersionNum[i]) : 0;
            int currentPart = i < versionNum.length ? Integer.parseInt(versionNum[i]) : 0;
            if (newPart > currentPart)
                return true;
            if (currentPart > newPart)
                return false;
        }
        return false;
    }

}
