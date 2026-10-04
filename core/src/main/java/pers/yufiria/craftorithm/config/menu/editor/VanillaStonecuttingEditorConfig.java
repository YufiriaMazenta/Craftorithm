package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/editor/vanilla_stonecutting.yml")
public class VanillaStonecuttingEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_stonecutting> - <recipe_key> - <translate:lang:menu.recipe_editor.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", MenuIconConfigUtils.editorFrameIcon("vanilla_stonecutting"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", MenuIconConfigUtils.editorResultFrameIcon());
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", MenuIconConfigUtils.editorConfirmIcon("minecraft:stonecutter"));
    public static final ConfigSectionConfig BACK_ICON = new ConfigSectionConfig("back_icon", MenuIconConfigUtils.editorBackIcon());
    public static final ConfigSectionConfig DELETE_ICON = new ConfigSectionConfig("delete_icon", MenuIconConfigUtils.editorDeleteIcon());
}
