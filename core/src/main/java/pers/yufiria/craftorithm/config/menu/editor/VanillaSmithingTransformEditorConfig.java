package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/editor/vanilla_smithing_transform.yml")
public class VanillaSmithingTransformEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smithing_transform> - <recipe_key> - <translate:lang:menu.recipe_editor.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", MenuIconConfigUtils.editorFrameIcon("vanilla_smithing_transform"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", MenuIconConfigUtils.editorResultFrameIcon());
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", MenuIconConfigUtils.editorConfirmIcon("minecraft:smithing_table"));
    public static final ConfigSectionConfig BACK_ICON = new ConfigSectionConfig("back_icon", MenuIconConfigUtils.editorBackIcon());
    public static final ConfigSectionConfig DELETE_ICON = new ConfigSectionConfig("delete_icon", MenuIconConfigUtils.editorDeleteIcon());
}
