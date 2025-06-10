package pers.yufiria.craftorithm.ui.custom;

import crypticlib.ui.display.Icon;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public enum CustomMenuIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "recipe_display" -> {
                String recipeId = config.getString("recipe_id");
                NamespacedKey recipeKey = NamespacedKey.fromString(Objects.requireNonNull(recipeId));
                ClickType viewClick, editClick;
                if (config.contains("view_click")) {
                    viewClick = ClickType.valueOf(config.getString("view_click").toUpperCase());
                } else {
                    viewClick = null;
                }
                if (config.contains("edit_click")) {
                    editClick = ClickType.valueOf(config.getString("edit_click").toUpperCase());
                } else {
                    editClick = null;
                }
                List<String> extraLore = config.getStringList("extra_lore");
                return () -> new RecipeDisplayIcon(
                    recipeKey,
                    extraLore,
                    viewClick != null ? viewClick : ClickType.LEFT,
                    editClick != null ? editClick : ClickType.SHIFT_RIGHT
                );
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }
}
