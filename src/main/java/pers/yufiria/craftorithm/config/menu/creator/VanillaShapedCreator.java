package pers.yufiria.craftorithm.config.menu.creator;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "menus/internal/creator/vanilla_shaped.yml")
public class VanillaShapedCreator {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_shaped><translate:lang:menu.recipe_creator.name>");

}
