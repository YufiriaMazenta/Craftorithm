package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import com.github.yufiriamazenta.lib.ParettiaLib;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateUtil {

    public static void checkUpdate(CommandSender sender) {
        if (!Craftorithm.getInstance().getConfig().getBoolean("check_update"))
            return;
        ParettiaLib.INSTANCE.getPlatform().runTaskAsync(Craftorithm.getInstance(), task -> {
            try {
                URL url = new URL("https://api.github.com/repos/YufiriaMazenta/Craftorithm/releases/latest");
                URLConnection conn = url.openConnection();
                String newVersion;
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(60000);
                InputStream is = conn.getInputStream();
                newVersion = new BufferedReader(new InputStreamReader(is)).readLine();
                is.close();

                int index = newVersion.indexOf("\"tag_name\"");
                int index2 = newVersion.indexOf("\"target_commitish\"");
                newVersion = newVersion.substring(index + 13, index2 - 2);
                String ver = Craftorithm.getInstance().getDescription().getVersion();
                ver = ver.substring(0, ver.indexOf("-"));
                if (checkVersion(newVersion, ver)) {
                    String finalNewVersion = newVersion;
                    Bukkit.getScheduler().callSyncMethod(Craftorithm.getInstance(), () -> {
                        LangUtil.sendMsg(sender, "new_version", ContainerUtil.newHashMap("<new_version>", finalNewVersion));
                        return null;
                    });
                }
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
