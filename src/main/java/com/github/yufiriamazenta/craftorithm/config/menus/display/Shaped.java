package com.github.yufiriamazenta.craftorithm.config.menus.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.List;

@ConfigHandler(path = "menus/display/shaped.yml")
public class Shaped {

    public static final StringConfig TITLE = new StringConfig("title", "有序合成");

    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of(
        "####I####",
        "#AAA#####",
        "#AAA##R##",
        "#AAA#####",
        "#########"
    ));

    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons");

}
