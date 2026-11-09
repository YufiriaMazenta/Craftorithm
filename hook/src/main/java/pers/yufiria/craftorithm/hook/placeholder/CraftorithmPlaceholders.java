package pers.yufiria.craftorithm.hook.placeholder;

import crypticlib.Key;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeManager;

public class CraftorithmPlaceholders extends PlaceholderExpansion {

    public static final CraftorithmPlaceholders INSTANCE = new CraftorithmPlaceholders();

    private CraftorithmPlaceholders() {
    }

    @Override
    public @NotNull String getIdentifier() {
        return "craftorithm";
    }

    @Override
    public @NotNull String getAuthor() {
        return Craftorithm.instance().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return Craftorithm.instance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String type, argument;
        if (!params.contains("_")) {
            type = params;
            argument = null;
        } else {
            type = params.substring(0, params.indexOf("_"));
            argument = params.substring(params.indexOf("_") + 1);
        }
        switch (type) {
            case "discovered" -> {
                if (player == null) {
                    return null;
                }
                if (argument == null) {
                    return String.valueOf(player.getDiscoveredRecipes().size());
                }
                if (argument.equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)) {
                    return String.valueOf(
                        player.getDiscoveredRecipes()
                            .stream()
                            .filter(
                                it -> it.getNamespace().equals(RecipeManager.INSTANCE.PLUGIN_RECIPE_NAMESPACE)
                            )
                            .count()
                    );
                }
                Key key = Key.key(argument);
                if (key == null) {
                    return null;
                }
                NamespacedKey recipeKey = new NamespacedKey(key.namespace(), key.key());
                return String.valueOf(player.hasDiscoveredRecipe(recipeKey));
            }
            default -> {
                return null;
            }
        }
    }
}
