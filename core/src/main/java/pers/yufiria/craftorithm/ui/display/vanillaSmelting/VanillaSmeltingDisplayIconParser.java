package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;
import java.util.function.Supplier;

public enum VanillaSmeltingDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "vanilla_smelting_ingredient" -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new VanillaSmeltingIngredientIcon(actions);
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
