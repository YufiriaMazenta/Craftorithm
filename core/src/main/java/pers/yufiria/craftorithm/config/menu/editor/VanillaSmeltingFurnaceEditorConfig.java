package pers.yufiria.craftorithm.config.menu.editor;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/editor/vanilla_smelting_furnace.yml")
public class VanillaSmeltingFurnaceEditorConfig {
    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smelting_furnace> - <recipe_key> - <translate:lang:menu.recipe_editor.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", () -> MenuIconConfigUtils.editorFrameIcon("vanilla_smelting_furnace"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", MenuIconConfigUtils::editorResultFrameIcon);
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", () -> MenuIconConfigUtils.editorConfirmIcon("minecraft:furnace"));
    public static final ConfigSectionConfig EXP_ICON = new ConfigSectionConfig("exp_icon", MenuIconConfigUtils::editorExpIcon);
    public static final ConfigSectionConfig TIME_ICON = new ConfigSectionConfig("time_icon", MenuIconConfigUtils::editorTimeIcon);
    public static final ConfigSectionConfig BACK_ICON = new ConfigSectionConfig("back_icon", MenuIconConfigUtils::editorBackIcon);
    public static final ConfigSectionConfig DELETE_ICON = new ConfigSectionConfig("delete_icon", MenuIconConfigUtils::editorDeleteIcon);

    public static final ConfigSectionConfig CATEGORY_ICON_FOOD = new ConfigSectionConfig("category_icon.food", () -> MenuIconConfigUtils.categoryIcon("minecraft:cooked_beef", "menu.recipe_editor.category", "cooking_category_name.food"));
    public static final ConfigSectionConfig CATEGORY_ICON_BLOCKS = new ConfigSectionConfig("category_icon.blocks", () -> MenuIconConfigUtils.categoryIcon("minecraft:stone", "menu.recipe_editor.category", "cooking_category_name.blocks"));
    public static final ConfigSectionConfig CATEGORY_ICON_MISC = new ConfigSectionConfig("category_icon.misc", () -> MenuIconConfigUtils.categoryIcon("minecraft:stick", "menu.recipe_editor.category", "cooking_category_name.misc"));
}
