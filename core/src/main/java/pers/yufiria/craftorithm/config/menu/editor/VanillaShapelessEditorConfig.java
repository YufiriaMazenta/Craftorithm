package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/editor/vanilla_shapeless.yml")
public class VanillaShapelessEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_shapeless> - <recipe_key> - <translate:lang:menu.recipe_editor.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", MenuIconConfigUtils.editorFrameIcon("vanilla_shapeless"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", MenuIconConfigUtils.editorResultFrameIcon());
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", MenuIconConfigUtils.editorConfirmIcon("minecraft:crafting_table"));
    public static final ConfigSectionConfig BACK_ICON = new ConfigSectionConfig("back_icon", MenuIconConfigUtils.editorBackIcon());
    public static final ConfigSectionConfig DELETE_ICON = new ConfigSectionConfig("delete_icon", MenuIconConfigUtils.editorDeleteIcon());

    public static final ConfigSectionConfig CATEGORY_ICON_MISC = new ConfigSectionConfig("category_icon.misc", MenuIconConfigUtils.categoryIcon("minecraft:apple", "menu.recipe_editor.category", "crafting_category_name.misc"));
    public static final ConfigSectionConfig CATEGORY_ICON_BUILDING = new ConfigSectionConfig("category_icon.building", MenuIconConfigUtils.categoryIcon("minecraft:bricks", "menu.recipe_editor.category", "crafting_category_name.building"));
    public static final ConfigSectionConfig CATEGORY_ICON_REDSTONE = new ConfigSectionConfig("category_icon.redstone", MenuIconConfigUtils.categoryIcon("minecraft:redstone", "menu.recipe_editor.category", "crafting_category_name.redstone"));
    public static final ConfigSectionConfig CATEGORY_ICON_EQUIPMENT = new ConfigSectionConfig("category_icon.equipment", MenuIconConfigUtils.categoryIcon("minecraft:iron_axe", "menu.recipe_editor.category", "crafting_category_name.tools"));
}
