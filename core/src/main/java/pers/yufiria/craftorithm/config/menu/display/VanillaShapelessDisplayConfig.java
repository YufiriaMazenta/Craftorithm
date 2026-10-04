package pers.yufiria.craftorithm.config.menu.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;


@ConfigHandler(path = "menus/internal/display/vanilla_shapeless.yml")
public class VanillaShapelessDisplayConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_shapeless>:<recipe_key>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of("########X", "#ABC#####", "#DEF###R#", "#GHI#####", "#########"));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", () -> {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon("minecraft:green_stained_glass_pane", "&a<translate:lang:recipe_type_name.vanilla_shapeless>"));
        config.set("A.icon_type", "vanilla_shapeless_ingredient");
        config.set("A.ingredient_slot", 0);
        config.set("B.icon_type", "vanilla_shapeless_ingredient");
        config.set("B.ingredient_slot", 1);
        config.set("C.icon_type", "vanilla_shapeless_ingredient");
        config.set("C.ingredient_slot", 2);
        config.set("D.icon_type", "vanilla_shapeless_ingredient");
        config.set("D.ingredient_slot", 3);
        config.set("E.icon_type", "vanilla_shapeless_ingredient");
        config.set("E.ingredient_slot", 4);
        config.set("F.icon_type", "vanilla_shapeless_ingredient");
        config.set("F.ingredient_slot", 5);
        config.set("G.icon_type", "vanilla_shapeless_ingredient");
        config.set("G.ingredient_slot", 6);
        config.set("H.icon_type", "vanilla_shapeless_ingredient");
        config.set("H.ingredient_slot", 7);
        config.set("I.icon_type", "vanilla_shapeless_ingredient");
        config.set("I.ingredient_slot", 8);
        config.set("R.icon_type", "result");
        MenuIconConfigUtils.setDisplayBackButton(config, "X");
        return config;
    });

}
