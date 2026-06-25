package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/editor/anvil.yml")
public class AnvilEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.anvil> - <recipe_key> - 编辑器");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon");
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon");
    public static final ConfigSectionConfig COST_LEVEL_ICON = new ConfigSectionConfig("cost_level_icon");
    public static final ConfigSectionConfig BACK_ICON = new ConfigSectionConfig("back_icon");
}
