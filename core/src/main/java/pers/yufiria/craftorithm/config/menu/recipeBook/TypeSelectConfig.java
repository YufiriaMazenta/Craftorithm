package pers.yufiria.craftorithm.config.menu.recipeBook;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.List;

@ConfigHandler(path = "menus/internal/recipeBook/type_select.yml")
public class TypeSelectConfig {

    public static final StringConfig TITLE = new StringConfig("title", "&6配方书 - 选择类型");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of(
        "#########",
        "#RRRRRRR#",
        "#RRRRRRR#",
        "#RRRRRRR#",
        "##P###N##"
    ));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons");

}
