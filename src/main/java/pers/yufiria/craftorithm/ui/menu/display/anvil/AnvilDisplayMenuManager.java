package pers.yufiria.craftorithm.ui.menu.display.anvil;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.Anvil;
import pers.yufiria.craftorithm.recipe.extra.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.ui.MenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum AnvilDisplayMenuManager implements MenuManager<AnvilRecipe>, BukkitLifeCycleTask {

    INSTANCE;

    private MenuDisplay anvilDisplayMenuDisplay;

    @Override
    public IconParser iconParser() {
        return AnvilDisplayIconParser.INSTANCE;
    }

    @Override
    public void openMenu(Player player, AnvilRecipe recipe) {
        if (anvilDisplayMenuDisplay == null) {
            anvilDisplayMenuDisplay = loadMenuDisplay(Anvil.TITLE.value(), Anvil.LAYOUT.value(), Anvil.ICONS.value());
        }
        new AnvilDisplayMenu(player, anvilDisplayMenuDisplay, recipe).openMenu();
    }

    @Override
    public void run(Plugin plugin, LifeCycle lifeCycle) {
        anvilDisplayMenuDisplay = loadMenuDisplay(Anvil.TITLE.value(), Anvil.LAYOUT.value(), Anvil.ICONS.value());
    }

}
