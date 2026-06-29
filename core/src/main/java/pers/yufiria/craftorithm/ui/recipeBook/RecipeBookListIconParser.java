package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public enum RecipeBookListIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        return switch (iconType) {
            case "recipe_display" -> parseRecipeDisplayIcon(config);
            case "prev_page" -> parsePrevPageIcon(config);
            case "next_page" -> parseNextPageIcon(config);
            case "sort" -> parseSortIcon(config);
            case "back" -> parseBackIcon(config);
            default -> IconParser.super.parse(config);
        };
    }

    private Supplier<Icon> parseRecipeDisplayIcon(ConfigurationSection config) {
        ClickType viewClick = ClickType.valueOf(config.getString("view_click", "LEFT").toUpperCase());
        ClickType editClick = ClickType.valueOf(config.getString("edit_click", "RIGHT").toUpperCase());
        List<String> extraLore = config.getStringList("extra_lore");
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));

        return () -> new RecipeDisplayIcon(null, extraLore, viewClick, editClick, actions);
    }

    private Supplier<Icon> parsePrevPageIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new PrevPageIcon(iconDisplay, actions);
    }

    private Supplier<Icon> parseNextPageIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new NextPageIcon(iconDisplay, actions);
    }

    private Supplier<Icon> parseSortIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new SortIcon(iconDisplay, actions);
    }

    private Supplier<Icon> parseBackIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new BackIcon(iconDisplay, actions);
    }

}
