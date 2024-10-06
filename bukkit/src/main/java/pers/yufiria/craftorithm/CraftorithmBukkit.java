package pers.yufiria.craftorithm;

import crypticlib.BukkitPlugin;

public class CraftorithmBukkit extends BukkitPlugin {

    private static CraftorithmBukkit INSTANCE;

    public CraftorithmBukkit() {
        INSTANCE = this;
    }

    public static CraftorithmBukkit instance() {
        return INSTANCE;
    }

}
