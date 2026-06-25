package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/editor/vanilla_smelting_campfire.yml")
public class VanillaSmeltingCampfireEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smelting_campfire> - <recipe_key> - 编辑器");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon");
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon");
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon");
    public static final ConfigSectionConfig EXP_ICON = new ConfigSectionConfig("exp_icon");
    public static final ConfigSectionConfig TIME_ICON = new ConfigSectionConfig("time_icon");
    public static final ConfigSectionConfig BACK_ICON = new ConfigSectionConfig("back_icon");

    public static final ConfigSectionConfig CATEGORY_ICON_FOOD = new ConfigSectionConfig("category_icon.food");
    public static final ConfigSectionConfig CATEGORY_ICON_BLOCKS = new ConfigSectionConfig("category_icon.blocks");
    public static final ConfigSectionConfig CATEGORY_ICON_MISC = new ConfigSectionConfig("category_icon.misc");
}
