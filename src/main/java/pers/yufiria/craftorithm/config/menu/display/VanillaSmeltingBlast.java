package pers.yufiria.craftorithm.config.menu.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.List;

@ConfigHandler(path = "menus/internal/display/vanilla_smelting_blast.yml")
public class VanillaSmeltingBlast {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smelting_blast>:<recipe_key>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of("#########", "##A###R##", "#########"));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons");

}
