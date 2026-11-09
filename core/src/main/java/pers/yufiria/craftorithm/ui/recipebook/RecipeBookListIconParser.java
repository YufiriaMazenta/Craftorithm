package pers.yufiria.craftorithm.ui.recipebook;

import java.util.Locale;
import crypticlib.script.compile.CompiledScript;
import crypticlib.ui.display.IconCreator;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.RecipeDisplayIcon;

import java.util.List;
import java.util.Map;

public enum RecipeBookListIconParser implements IconParser {

    INSTANCE;

    @Override
    public IconCreator parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase(Locale.ROOT);
        return switch (iconType) {
            case "recipe_display" -> parseRecipeDisplayIcon(config);
            case "prev_page" -> parsePrevPageIcon(config);
            case "next_page" -> parseNextPageIcon(config);
            case "sort" -> parseSortIcon(config);
            case "back" -> parseBackIcon(config);
            default -> IconParser.super.parse(config);
        };
    }

    private IconCreator parseRecipeDisplayIcon(ConfigurationSection config) {
        ClickType viewClick = ClickType.valueOf(config.getString("view_click", "LEFT").toUpperCase(Locale.ROOT));
        ClickType editClick = ClickType.valueOf(config.getString("edit_click", "RIGHT").toUpperCase(Locale.ROOT));
        List<String> extraLore = config.getStringList("extra_lore");
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));

        return () -> new RecipeDisplayIcon(null, extraLore, viewClick, editClick, actions);
    }

    private IconCreator parsePrevPageIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new PrevPageIcon(iconDisplay, actions);
    }

    private IconCreator parseNextPageIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new NextPageIcon(iconDisplay, actions);
    }

    private IconCreator parseSortIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new SortIcon(iconDisplay, actions);
    }

    private IconCreator parseBackIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        Map<ClickType, CompiledScript> actions = parseActions(config.getConfigurationSection("actions"));
        return () -> new BackIcon(iconDisplay, actions);
    }

}
