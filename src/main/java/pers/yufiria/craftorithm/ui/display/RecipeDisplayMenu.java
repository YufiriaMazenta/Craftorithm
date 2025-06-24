package pers.yufiria.craftorithm.ui.display;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.TranslatableMenu;

public class RecipeDisplayMenu<T extends Recipe> extends TranslatableMenu implements BackableMenu {

    protected Menu parentMenu;
    protected final T recipe;

    public RecipeDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, T recipe) {
        super(player, display);
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
