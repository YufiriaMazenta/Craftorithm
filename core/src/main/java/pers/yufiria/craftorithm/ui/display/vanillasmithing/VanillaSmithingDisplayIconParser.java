package pers.yufiria.craftorithm.ui.display.vanillasmithing;

import crypticlib.script.compile.CompiledScript;
import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.function.Supplier;

public enum VanillaSmithingDisplayIconParser implements IconParser {

    INSTANCE;

    public static final String ICON_TYPE_BASE = "vanilla_smithing_base",
        ICON_TYPE_ADDITION = "vanilla_smithing_addition",
        ICON_TYPE_TEMPLATE = "vanilla_smithing_template";

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case ICON_TYPE_BASE, ICON_TYPE_ADDITION, ICON_TYPE_TEMPLATE, RecipeDisplayManager.ICON_TYPE_RESULT -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> {
                    ItemDisplayIcon icon = new ItemDisplayIcon(actions);
                    icon.putData("icon_type", iconType);
                    return icon;
                };
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }

}
