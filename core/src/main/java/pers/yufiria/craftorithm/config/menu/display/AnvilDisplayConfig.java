package pers.yufiria.craftorithm.config.menu.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;

@ConfigHandler(path = "menus/internal/display/anvil.yml")
public class AnvilDisplayConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.anvil>:<recipe_key>, <translate:lang:menu.recipe_display.anvil.cost_level>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of("########X", "#A#B###C#", "#########"));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", () -> {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon(
            "minecraft:green_stained_glass_pane",
            "&a<translate:lang:recipe_type_name.anvil>",
            "<translate:lang:menu.recipe_display.anvil.cost_level>"
        ));
        config.set("A.icon_type", "anvil_base");
        config.set("B.icon_type", "anvil_addition");
        config.set("C.icon_type", "result");
        MenuIconConfigUtils.setDisplayBackButton(config, "X");
        return config;
    });

}
