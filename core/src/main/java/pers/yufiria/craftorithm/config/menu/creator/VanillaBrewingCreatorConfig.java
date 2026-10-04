package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/creator/vanilla_brewing.yml")
public class VanillaBrewingCreatorConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_brewing><translate:lang:menu.recipe_creator.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", () -> MenuIconConfigUtils.creatorFrameIcon("vanilla_brewing", "brewing_input", "brewing_ingredient", "result_right", "confirm_button"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", () -> MenuIconConfigUtils.creatorResultFrameIcon("brewing"));
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", () -> MenuIconConfigUtils.creatorConfirmIcon("minecraft:brewing_stand", "brewing"));

}
