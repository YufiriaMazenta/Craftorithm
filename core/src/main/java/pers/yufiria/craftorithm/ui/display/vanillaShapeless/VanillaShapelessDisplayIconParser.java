package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;
import java.util.function.Supplier;

public enum VanillaShapelessDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "vanilla_shapeless_ingredient" -> {
                int ingredientId = config.getInt("ingredient_slot", 0);
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new VanillaShapelessIngredientIcon(ingredientId, actions);
            }
            case "result" -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new RecipeResultIcon(actions);
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }

}
