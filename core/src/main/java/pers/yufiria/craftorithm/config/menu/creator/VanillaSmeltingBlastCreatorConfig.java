package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/creator/vanilla_smelting_blast.yml")
public class VanillaSmeltingBlastCreatorConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smelting_blast><translate:lang:menu.recipe_creator.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", MenuIconConfigUtils.creatorFrameIcon("vanilla_smelting_blast", "smelting", "result_right", "confirm_button"));
    public static final ConfigSectionConfig RESULT_FRAME_ICON = new ConfigSectionConfig("result_frame_icon", MenuIconConfigUtils.creatorResultFrameIcon("smelting"));
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", MenuIconConfigUtils.creatorConfirmIcon("minecraft:blast_furnace", "blasting"));
    public static final ConfigSectionConfig EXP_ICON = new ConfigSectionConfig("exp_icon", MenuIconConfigUtils.creatorExpIcon());
    public static final ConfigSectionConfig TIME_ICON = new ConfigSectionConfig("time_icon", MenuIconConfigUtils.creatorTimeIcon());
    public static final IntConfig DEFAULT_EXP = new IntConfig("default_exp", 1);
    public static final IntConfig DEFAULT_TIME = new IntConfig("default_time", 100);

    public static final ConfigSectionConfig CATEGORY_ICON_FOOD = new ConfigSectionConfig("category_icon.food", MenuIconConfigUtils.categoryIcon("minecraft:cooked_beef", "menu.recipe_creator.category", "cooking_category_name.food"));
    public static final ConfigSectionConfig CATEGORY_ICON_BLOCKS = new ConfigSectionConfig("category_icon.blocks", MenuIconConfigUtils.categoryIcon("minecraft:stone", "menu.recipe_creator.category", "cooking_category_name.blocks"));
    public static final ConfigSectionConfig CATEGORY_ICON_MISC = new ConfigSectionConfig("category_icon.misc", MenuIconConfigUtils.categoryIcon("minecraft:stick", "menu.recipe_creator.category", "cooking_category_name.misc"));

}
