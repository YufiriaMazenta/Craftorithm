package pers.yufiria.craftorithm.ui.display;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.RecipeManager;

public class RecipeDisplayMenu<T extends Recipe> extends Menu {

    protected final T recipe;

    public RecipeDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, T recipe) {
        super(player, display);
        this.recipe = recipe;
    }

    @Override
    public String parsedMenuTitle() {
        String title = this.display.title();
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        title = title.replace("<recipe_key>", recipeKey != null ? recipeKey.getKey() : "<recipe_key>");
        Player player = this.player();
        title = LangManager.INSTANCE.replaceLang(title, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }
}
