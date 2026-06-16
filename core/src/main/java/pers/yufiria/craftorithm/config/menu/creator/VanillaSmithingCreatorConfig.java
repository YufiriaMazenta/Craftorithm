package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/creator/vanilla_smithing.yml")
public class VanillaSmithingCreatorConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smithing><translate:lang:menu.recipe_creator.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon");
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon");
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon");

}
