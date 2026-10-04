package pers.yufiria.craftorithm.config.menu.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;

@ConfigHandler(path = "menus/internal/display/vanilla_smithing_transform.yml")
public class VanillaSmithingDisplayConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smithing_transform>:<recipe_key>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of("########X", "#ABC###R#", "#########"));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", () -> {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon("minecraft:green_stained_glass_pane", "&a<translate:lang:recipe_type_name.vanilla_smithing_transform>"));
        config.set("A.icon_type", "vanilla_smithing_template");
        config.set("B.icon_type", "vanilla_smithing_base");
        config.set("C.icon_type", "vanilla_smithing_addition");
        config.set("R.icon_type", "result");
        MenuIconConfigUtils.setDisplayBackButton(config, "X");
        return config;
    });

}
