package pers.yufiria.craftorithm.ui;

import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

public interface RecipeDisplayMenuManager<R extends Recipe> extends RecipeDisplayLoader {

    Menu openMenu(Player player, R recipe);

}
