package pers.yufiria.craftorithm.config.menu.recipobook;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;

@ConfigHandler(path = "menus/internal/recipe_book/type_select.yml")
public class TypeSelectConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:menu.recipe_book.select_title>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of(
        "#########",
        "#ABCDEFG#",
        "#HIJKLMN#",
        "#########"
    ));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", buildIcons());

    private static ConfigurationSection buildIcons() {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon("minecraft:gray_stained_glass_pane", "&r"));
        setRecipeListIcon(config, "A", "vanilla_shaped", "minecraft:crafting_table");
        setRecipeListIcon(config, "B", "vanilla_shapeless", "minecraft:crafting_table");
        setRecipeListIcon(config, "C", "vanilla_smelting_furnace", "minecraft:furnace");
        setRecipeListIcon(config, "D", "vanilla_smelting_blast", "minecraft:blast_furnace");
        setRecipeListIcon(config, "E", "vanilla_smelting_smoker", "minecraft:smoker");
        setRecipeListIcon(config, "F", "vanilla_smelting_campfire", "minecraft:campfire");
        setRecipeListIcon(config, "G", "vanilla_smithing_transform", "minecraft:smithing_table");
        setRecipeListIcon(config, "H", "vanilla_stonecutting", "minecraft:stonecutter");
        setRecipeListIcon(config, "I", "vanilla_brewing", "minecraft:brewing_stand");
        setRecipeListIcon(config, "J", "anvil", "minecraft:anvil");
        return config;
    }

    private static void setRecipeListIcon(ConfigurationSection config, String key, String recipeType, String material) {
        config.set(key + ".icon_type", "recipe_list");
        config.set(key + ".recipe_type", recipeType);
        config.set(key + ".material", material);
        config.set(key + ".name", "&a<translate:lang:recipe_type_name." + recipeType + ">");
        config.set(key + ".lore", List.of(
            "<translate:lang:menu.recipe_book.recipe_count>",
            "<translate:lang:menu.common.click_to_view>"
        ));
    }

}
