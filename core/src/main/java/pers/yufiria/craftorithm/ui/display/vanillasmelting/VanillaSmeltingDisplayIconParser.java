package pers.yufiria.craftorithm.ui.display.vanillasmelting;

import crypticlib.ui.display.IconCreator;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Set;

public enum VanillaSmeltingDisplayIconParser implements IconParser {

    INSTANCE;

    public static final String ICON_TYPE_INGREDIENT = "vanilla_smelting_ingredient";

    @Override
    public IconCreator parse(ConfigurationSection config) {
        return parseItemDisplayIcon(
            config,
            Set.of(ICON_TYPE_INGREDIENT, RecipeDisplayManager.ICON_TYPE_RESULT)
        );
    }

}