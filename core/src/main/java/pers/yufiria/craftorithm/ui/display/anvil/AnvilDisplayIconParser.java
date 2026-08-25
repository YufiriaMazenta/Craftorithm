package pers.yufiria.craftorithm.ui.display.anvil;

import crypticlib.script.compile.CompiledScript;
import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.function.Supplier;

public enum AnvilDisplayIconParser implements IconParser {

    INSTANCE;

    public static final String ICON_TYPE_BASE = "anvil_base", ICON_TYPE_ADDITION = "anvil_addition";

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case ICON_TYPE_ADDITION, ICON_TYPE_BASE, RecipeDisplayManager.ICON_TYPE_RESULT -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> {
                    ItemDisplayIcon baseIcon = new ItemDisplayIcon(actions);
                    baseIcon.putData("icon_type", iconType);
                    return baseIcon;
                };
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }

}
