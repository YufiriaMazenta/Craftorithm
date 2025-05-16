package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.display.vanillaSmelting.VanillaSmeltingIngredientIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.function.Supplier;

public enum VanillaSmithingDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "vanilla_smithing_base" -> {
                return VanillaSmithingBaseIcon::new;
            }
            case "vanilla_smithing_addition" -> {
                return VanillaSmithingAdditionIcon::new;
            }
            case "vanilla_smithing_template" -> {
                return VanillaSmithingTemplateIcon::new;
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
