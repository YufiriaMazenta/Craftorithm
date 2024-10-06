package pers.yufiria.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.BooleanConfig;

@ConfigHandler(path = "config.yml")
public class Configs {

    public static final BooleanConfig checkUpdate = new BooleanConfig("check-update", true);
    public static final BooleanConfig bstats = new BooleanConfig("bstats", true);

}
