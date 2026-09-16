package pers.yufiria.craftorithm.ui.display.vanillabrewing;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Set;
import java.util.function.Supplier;

public enum VanillaBrewingDisplayIconParser implements IconParser {

    INSTANCE;

    public static final String ICON_TYPE_INPUT = "vanilla_brewing_input", ICON_TYPE_INGREDIENT = "vanilla_brewing_ingredient";

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        return parseItemDisplayIcon(
            config,
            Set.of(ICON_TYPE_INPUT, ICON_TYPE_INGREDIENT, RecipeDisplayManager.ICON_TYPE_RESULT)
        );
    }

}