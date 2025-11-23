package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/creator/tag_select.yml")
public class TagSelectConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:menu.tag_select.name>");
    public static final ConfigSectionConfig PREVIOUS_ICON = new ConfigSectionConfig("previous_icon");
    public static final ConfigSectionConfig NEXT_ICON = new ConfigSectionConfig("next_icon");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon");

}
