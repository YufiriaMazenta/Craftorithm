package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.ui.display.Icon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.display.RecipeResultIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;
import java.util.function.Supplier;

public enum VanillaSmithingDisplayIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "vanilla_smithing_base" -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new VanillaSmithingBaseIcon(actions);
            }
            case "vanilla_smithing_addition" -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new VanillaSmithingAdditionIcon(actions);
            }
            case "vanilla_smithing_template" -> {
                Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
                return () -> new VanillaSmithingTemplateIcon(actions);
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
