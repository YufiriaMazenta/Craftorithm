package pers.yufiria.craftorithm.ui.display;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.display.MenuLayout;
import crypticlib.ui.menu.Menu;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.MenuDisplayLoader;
import pers.yufiria.craftorithm.ui.TranslatableMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class RecipeDisplayMenu<T extends Recipe> extends TranslatableMenu implements BackableMenu, MenuDisplayLoader {

    protected Menu parentMenu;
    protected final T recipe;

    public RecipeDisplayMenu(@NotNull Player player, T recipe) {
        super(player);
        this.recipe = recipe;
    }

    @Override
    public String parsedMenuTitle() {
        String title = super.parsedMenuTitle();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        title = title.replace("<recipe_key>", recipeKey != null ? recipeKey.getKey() : "<recipe_key>");
        return title;
    }

    @Override
    public Menu parentMenu() {
        return parentMenu;
    }

    @Override
    public void setParentMenu(@Nullable Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

}
