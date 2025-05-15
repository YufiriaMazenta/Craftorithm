package pers.yufiria.craftorithm.ui.display.vanillaShaped;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.function.Supplier;

public enum VanillaShapedDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "vanilla_shaped_ingredient" -> {
                int ingredientId = config.getInt("ingredient_slot", 0);
                return () -> new VanillaShapedIngredientIcon(ingredientId);
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
