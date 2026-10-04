package pers.yufiria.craftorithm.config.menu.recipobook;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;

@ConfigHandler(path = "menus/internal/recipe_book/recipe_list.yml")
public class RecipeListConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:menu.recipe_book.title>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of(
        "#########",
        "#RRRRRRR#",
        "#RRRRRRR#",
        "#RRRRRRR#",
        "#BS###PN#"
    ));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", () -> {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon("minecraft:gray_stained_glass_pane", "&r"));
        config.set("R.icon_type", "recipe_display");
        config.set("R.view_click", "left");
        config.set("R.edit_click", "right");
        config.set("R.extra_lore", List.of(
            "&r",
            "<translate:lang:menu.recipe_book.click_recipe>"
        ));
        config.set("S.icon_type", "sort");
        config.set("S.material", "minecraft:hopper");
        config.set("S.name", "<translate:lang:menu.recipe_book.sort_name>");
        config.set("S.lore", List.of("<translate:lang:menu.recipe_book.sort_lore>"));
        config.set("P.icon_type", "prev_page");
        config.set("P.material", "minecraft:arrow");
        config.set("P.name", "<translate:lang:menu.common.prev_page>");
        config.set("N.icon_type", "next_page");
        config.set("N.material", "minecraft:arrow");
        config.set("N.name", "<translate:lang:menu.common.next_page>");
        config.set("B.icon_type", "back");
        config.set("B.material", "minecraft:barrier");
        config.set("B.name", "<translate:lang:menu.common.back>");
        return config;
    });

}
