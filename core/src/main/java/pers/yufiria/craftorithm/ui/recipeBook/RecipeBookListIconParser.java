package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.ui.icon.*;

import java.util.List;
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

        return () -> new RecipeDisplayIcon(null, extraLore, viewClick, editClick);
    }

    private Supplier<Icon> parsePrevPageIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new PrevPageIcon(iconDisplay);
    }

    private Supplier<Icon> parseNextPageIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new NextPageIcon(iconDisplay);
    }

    private Supplier<Icon> parseSortIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new SortIcon(iconDisplay);
    }

    private Supplier<Icon> parseBackIcon(ConfigurationSection config) {
        IconDisplay iconDisplay = parseIconDisplay(config);
        return () -> new BackIcon(iconDisplay);
    }

}
