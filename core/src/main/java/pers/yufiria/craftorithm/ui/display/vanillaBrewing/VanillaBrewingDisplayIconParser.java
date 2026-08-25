package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import crypticlib.script.compile.CompiledScript;
import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.function.Supplier;

public enum VanillaBrewingDisplayIconParser implements IconParser {

    INSTANCE;

    public static final String ICON_TYPE_INPUT = "vanilla_brewing_input", ICON_TYPE_INGREDIENT = "vanilla_brewing_ingredient";

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case ICON_TYPE_INPUT, ICON_TYPE_INGREDIENT, RecipeDisplayManager.ICON_TYPE_RESULT -> {
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
