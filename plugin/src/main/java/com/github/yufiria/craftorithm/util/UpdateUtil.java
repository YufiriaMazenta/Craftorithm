package com.github.yufiria.craftorithm.util;

import com.github.yufiria.craftorithm.Craftorithm;
import crypticlib.CrypticLib;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateUtil {

    public static void pullUpdateCheckRequest(CommandSender sender) {
//        if (!PluginConfigs.CHECK_UPDATE.value())
//            return;
        CrypticLib.platform().scheduler().runTaskAsync(Craftorithm.instance(), () -> {
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
                    //TODO 发送提醒
//                    CrypticLib.platform().scheduler().runTask(Craftorithm.instance(), () -> {
//                        LangUtil.sendLang(sender, Languages.NEW_VERSION, CollectionsUtil.newStringHashMap("<new_version>", latestVersion));
//                    });
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
