package pers.yufiria.craftorithm.ui.display.anvil;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Set;
import java.util.function.Supplier;

public enum AnvilDisplayIconParser implements IconParser {

    INSTANCE;

    public static final String ICON_TYPE_BASE = "anvil_base", ICON_TYPE_ADDITION = "anvil_addition";

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        return parseItemDisplayIcon(
            config,
            Set.of(ICON_TYPE_ADDITION, ICON_TYPE_BASE, RecipeDisplayManager.ICON_TYPE_RESULT)
        );
    }

}