package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.function.Supplier;

public enum VanillaShapelessDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "vanilla_shapeless_ingredient" -> {
                int ingredientId = config.getInt("ingredient_slot", 0);
                return () -> new VanillaShapelessIngredientIcon(ingredientId);
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
