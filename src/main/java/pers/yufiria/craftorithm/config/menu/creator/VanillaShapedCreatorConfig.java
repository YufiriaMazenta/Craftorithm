package pers.yufiria.craftorithm.config.menu.creator;

import com.willfp.eco.core.config.Configs;
import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/creator/vanilla_shaped.yml")
public class VanillaShapedCreatorConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_shaped><translate:lang:menu.recipe_creator.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon");
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon");
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon");

    public static final ConfigSectionConfig CATEGORY_ICON_MISC = new ConfigSectionConfig("category_icon.misc");
    public static final ConfigSectionConfig CATEGORY_ICON_BUILDING = new ConfigSectionConfig("category_icon.building");
    public static final ConfigSectionConfig CATEGORY_ICON_REDSTONE = new ConfigSectionConfig("category_icon.redstone");
    public static final ConfigSectionConfig CATEGORY_ICON_EQUIPMENT = new ConfigSectionConfig("category_icon.equipment");

}
