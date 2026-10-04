package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/creator/vanilla_shapeless.yml")
public class VanillaShapelessCreatorConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_shapeless><translate:lang:menu.recipe_creator.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", MenuIconConfigUtils.creatorFrameIcon("vanilla_shapeless", "crafting", "result_right", "confirm_button"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", MenuIconConfigUtils.creatorResultFrameIcon("shaped"));
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", MenuIconConfigUtils.creatorConfirmIcon("minecraft:crafting_table", "shapeless"));

    public static final ConfigSectionConfig CATEGORY_ICON_MISC = new ConfigSectionConfig("category_icon.misc", MenuIconConfigUtils.categoryIcon("minecraft:apple", "menu.recipe_creator.category", "crafting_category_name.misc"));
    public static final ConfigSectionConfig CATEGORY_ICON_BUILDING = new ConfigSectionConfig("category_icon.building", MenuIconConfigUtils.categoryIcon("minecraft:bricks", "menu.recipe_creator.category", "crafting_category_name.building"));
    public static final ConfigSectionConfig CATEGORY_ICON_REDSTONE = new ConfigSectionConfig("category_icon.redstone", MenuIconConfigUtils.categoryIcon("minecraft:redstone", "menu.recipe_creator.category", "crafting_category_name.redstone"));
    public static final ConfigSectionConfig CATEGORY_ICON_EQUIPMENT = new ConfigSectionConfig("category_icon.equipment", MenuIconConfigUtils.categoryIcon("minecraft:iron_axe", "menu.recipe_creator.category", "crafting_category_name.tools"));

}
