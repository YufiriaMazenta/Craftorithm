package pers.yufiria.craftorithm.ui.custom;

import crypticlib.ui.display.Icon;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.ui.icon.IconParser;

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
                return () -> new RecipeDisplayIcon(recipeKey);
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }
}
