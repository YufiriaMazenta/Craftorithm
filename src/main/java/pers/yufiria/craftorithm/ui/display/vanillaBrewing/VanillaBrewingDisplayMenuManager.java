package pers.yufiria.craftorithm.ui.display.vanillaBrewing;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaBrewing;
import pers.yufiria.craftorithm.recipe.extra.BrewingRecipe;
import pers.yufiria.craftorithm.ui.MenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD)
    }
)
public enum VanillaBrewingDisplayMenuManager implements MenuManager<BrewingRecipe>, BukkitLifeCycleTask {

    INSTANCE;
    private MenuDisplay vanillaBrewingDisplay;

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        this.vanillaBrewingDisplay = loadMenuDisplay(VanillaBrewing.TITLE.value(), VanillaBrewing.LAYOUT.value(), VanillaBrewing.ICONS.value());
    }

    @Override
    public IconParser iconParser() {
        return VanillaBrewingDisplayIconParser.INSTANCE;
    }

    @Override
    public void openMenu(Player player, BrewingRecipe recipe) {
        if (vanillaBrewingDisplay == null) {
            this.vanillaBrewingDisplay = loadMenuDisplay(VanillaBrewing.TITLE.value(), VanillaBrewing.LAYOUT.value(), VanillaBrewing.ICONS.value());
        }
        new VanillaBrewingDisplayMenu(player, vanillaBrewingDisplay, recipe).openMenu();
    }

}
