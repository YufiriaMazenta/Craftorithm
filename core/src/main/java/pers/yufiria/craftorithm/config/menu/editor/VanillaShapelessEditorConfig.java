package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/editor/vanilla_shapeless.yml")
public class VanillaShapelessEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_shapeless> - <recipe_key> - 编辑器");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon");
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon");
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon");
}
