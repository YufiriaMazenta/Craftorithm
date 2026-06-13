package pers.yufiria.craftorithm.util;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;

public class ServerUtils {

    private static Boolean supportPotionMix;
    /**
     * 是否是1.20.1以后的paper服务端
     * 用于判断是否需要注册/删除时统一更新配方
     */
    public static boolean after1_20Paper() {
        return CrypticLibBukkit.isPaper() && MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20_1);
    }

    public static boolean supportPotionMix() {
        if (supportPotionMix == null) {
            try {
                Class.forName("io.papermc.paper.potion.PotionMix");
                supportPotionMix = true;
            } catch (ClassNotFoundException e) {
                supportPotionMix = false;
            }
        }
        return supportPotionMix;
    }

}
