package pers.yufiria.craftorithm.util;

public class ServerUtils {

    private static Boolean supportPotionMix;

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
