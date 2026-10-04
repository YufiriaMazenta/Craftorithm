package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

@ConfigHandler(path = "menus/internal/creator/anvil.yml")
public class AnvilCreatorConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.anvil><translate:lang:menu.recipe_creator.name>");
    public static final ConfigSectionConfig FRAME_ICON = new ConfigSectionConfig("frame_icon", () -> MenuIconConfigUtils.creatorFrameIcon("anvil", "anvil", "result_right", "confirm_button"));
    public static final ConfigSectionConfig CONFIRM_ICON = new ConfigSectionConfig("confirm_icon", () -> MenuIconConfigUtils.creatorConfirmIcon("minecraft:anvil", "anvil"));
    public static final ConfigSectionConfig COST_LEVEL_ICON = new ConfigSectionConfig("cost_level_icon", () -> MenuIconConfigUtils.icon(
        "minecraft:experience_bottle",
        "<translate:lang:menu.recipe_creator.anvil.cost_level>",
        "",
        "<translate:lang:menu.recipe_creator.anvil.cost_level_lore>"
    ));
    public static final IntConfig DEFAULT_COST_LEVEL = new IntConfig("default_cost_level", 1);

}
