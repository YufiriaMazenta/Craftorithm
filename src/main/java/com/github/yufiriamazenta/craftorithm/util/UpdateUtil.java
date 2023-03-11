package com.github.yufiriamazenta.craftorithm.util;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
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
        Bukkit.getScheduler().runTaskAsynchronously(Craftorithm.getInstance(), () -> {
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
                if (!ver.equals(newVersion)) {
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

}
