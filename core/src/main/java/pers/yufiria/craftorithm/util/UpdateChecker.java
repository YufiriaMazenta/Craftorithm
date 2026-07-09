package pers.yufiria.craftorithm.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.listener.EventListener;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@EventListener
public class UpdateChecker implements Listener {

    private static final String MODRINTH_API_URL = "https://api.modrinth.com/v2/project/craftorithm/version?limit=1&game_versions=";

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
                String mcVersion = MinecraftVersion.current().versionStr();
                mcVersion = URLEncoder.encode("[\"" + mcVersion + "\"]", StandardCharsets.UTF_8);
                URI uri = new URI(MODRINTH_API_URL + mcVersion);
                URLConnection conn = uri.toURL().openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                String latestVersion;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    JsonArray versions = JsonParser.parseReader(reader).getAsJsonArray();
                    if (versions.isEmpty()) {
                        Craftorithm.instance().getLogger().warning("No compatible version found on Modrinth for MC " + mcVersion);
                        return;
                    }
                    latestVersion = removeSuffixAfterDash(versions.get(0).getAsJsonObject().get("version_number").getAsString());
                }
                String pluginVersion = removeSuffixAfterDash(Craftorithm.instance().getDescription().getVersion());
                if (checkVersion(latestVersion, pluginVersion)) {
                    CrypticLibBukkit.scheduler().sync(() -> {
                        LangUtils.sendLang(sender, Languages.NEW_VERSION, CollectionsUtils.newStringHashMap("<new_version>", latestVersion));
                    });
                }
            } catch (Exception e) {
                Craftorithm.instance().getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    public static String removeSuffixAfterDash(String version) {
        if (version == null || version.isEmpty()) {
            return version;
        }
        int index = version.indexOf('-');
        if (index == -1) {
            return version; // 没有横杠，直接返回
        }
        return version.substring(0, index);
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