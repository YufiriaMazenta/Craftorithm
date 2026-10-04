package pers.yufiria.craftorithm.config.menu.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;

@ConfigHandler(path = "menus/internal/display/vanilla_brewing.yml")
public class VanillaBrewingDisplayConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_brewing>:<recipe_key>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of("########X", "##A######", "######C##", "##B######", "#########"));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", () -> {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon("minecraft:green_stained_glass_pane", "&a<translate:lang:recipe_type_name.vanilla_brewing>"));
        config.set("A.icon_type", "vanilla_brewing_ingredient");
        config.set("B.icon_type", "vanilla_brewing_input");
        config.set("C.icon_type", "result");
        MenuIconConfigUtils.setDisplayBackButton(config, "X");
        return config;
    });

}
