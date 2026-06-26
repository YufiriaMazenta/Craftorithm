package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Objects;
import java.util.function.Supplier;

public enum RecipeBookIconParser implements IconParser {

    INSTANCE;

    @Override
    public Supplier<Icon> parse(ConfigurationSection config) {
        String iconType = config.getString("icon_type", "common").toLowerCase();
        switch (iconType) {
            case "recipe_list" -> {
                String recipeTypeKey = config.getString("recipe_type");
                Objects.requireNonNull(recipeTypeKey, "recipe_list icon must have recipe_type");
                RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipeTypeKey);
                Objects.requireNonNull(recipeType, "Unknown recipe type: " + recipeTypeKey);
                IconDisplay iconDisplay = parseIconDisplay(config);
                return () -> new RecipeListIcon(iconDisplay, recipeType);
            }
            default -> {
                return IconParser.super.parse(config);
            }
        }
    }

}
