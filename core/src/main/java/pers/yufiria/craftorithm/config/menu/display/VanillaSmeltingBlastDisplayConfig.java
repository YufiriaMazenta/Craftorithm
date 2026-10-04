package pers.yufiria.craftorithm.config.menu.display;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import crypticlib.config.node.impl.bukkit.StringListConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import pers.yufiria.craftorithm.config.menu.MenuIconConfigUtils;

import java.util.List;

@ConfigHandler(path = "menus/internal/display/vanilla_smelting_blast.yml")
public class VanillaSmeltingBlastDisplayConfig {

    public static final StringConfig TITLE = new StringConfig("title", "<translate:lang:recipe_type_name.vanilla_smelting_blast>:<recipe_key>");
    public static final StringListConfig LAYOUT = new StringListConfig("layout", List.of("########X", "##A###R##", "#########"));
    public static final ConfigSectionConfig ICONS = new ConfigSectionConfig("icons", () -> {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("#", MenuIconConfigUtils.icon(
            "minecraft:green_stained_glass_pane",
            "&a<translate:lang:recipe_type_name.vanilla_smelting_blast>",
            "<translate:lang:menu.recipe_display.vanilla_smelting.time>",
            "<translate:lang:menu.recipe_display.vanilla_smelting.reward_exp>"
        ));
        config.set("A.icon_type", "vanilla_smelting_ingredient");
        config.set("R.icon_type", "result");
        MenuIconConfigUtils.setDisplayBackButton(config, "X");
        return config;
    });

}
