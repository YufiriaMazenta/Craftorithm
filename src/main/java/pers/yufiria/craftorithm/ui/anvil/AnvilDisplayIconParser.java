package pers.yufiria.craftorithm.ui.anvil;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.function.Supplier;

public enum AnvilDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "anvil_base" -> {
                return AnvilBaseIcon::new;
            }
            case "anvil_addition" -> {
                return AnvilAdditionIcon::new;
            }
            case "result" -> {
                return RecipeResultIcon::new;
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }

}
