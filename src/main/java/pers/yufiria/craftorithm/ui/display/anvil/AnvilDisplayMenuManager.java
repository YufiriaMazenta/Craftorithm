package pers.yufiria.craftorithm.ui.display.anvil;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.AnvilDisplay;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.ui.RecipeDisplayMenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum AnvilDisplayMenuManager implements RecipeDisplayMenuManager<AnvilRecipe>, BukkitLifeCycleTask {

    INSTANCE;

    private MenuDisplay anvilDisplayMenuDisplay;

    @Override
    public IconParser iconParser() {
        return AnvilDisplayIconParser.INSTANCE;
    }

    @Override
    public Menu openMenu(Player player, AnvilRecipe recipe) {
        if (anvilDisplayMenuDisplay == null) {
            anvilDisplayMenuDisplay = loadMenuDisplay(AnvilDisplay.TITLE.value(), AnvilDisplay.LAYOUT.value(), AnvilDisplay.ICONS.value());
        }
        AnvilDisplayMenu anvilDisplayMenu = new AnvilDisplayMenu(player, anvilDisplayMenuDisplay, recipe);
        anvilDisplayMenu.openMenu();
        return anvilDisplayMenu;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        anvilDisplayMenuDisplay = loadMenuDisplay(AnvilDisplay.TITLE.value(), AnvilDisplay.LAYOUT.value(), AnvilDisplay.ICONS.value());
    }

}
