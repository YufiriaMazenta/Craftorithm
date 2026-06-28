package pers.yufiria.craftorithm.config.menu.recipeBook;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;

import java.util.List;

@ConfigHandler(path = "menus/internal/recipeBook/recipe_list.yml")
public class RecipeListConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:menu.recipe_book.title>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of(
        "#########",
        "#RRRRRRR#",
        "#RRRRRRR#",
        "#RRRRRRR#",
        "#BS###PN#"
    ));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons");

}
