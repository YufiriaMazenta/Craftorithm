package pers.yufiria.craftorithm.ui.display.vanillaStonecutting;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaStonecuttingDisplay;
import pers.yufiria.craftorithm.ui.RecipeDisplayMenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE),
    @TaskRule(lifeCycle = LifeCycle.RELOAD)
})
public enum VanillaStonecuttingDisplayMenuManager implements RecipeDisplayMenuManager<StonecuttingRecipe>, BukkitLifeCycleTask {

    INSTANCE;
    private MenuDisplay vanillaStonecuttingDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaStonecuttingDisplayIconParser.INSTANCE;
    }

    @Override
    public Menu openMenu(Player player, StonecuttingRecipe recipe) {
        if (this.vanillaStonecuttingDisplay == null) {
            this.vanillaStonecuttingDisplay = loadMenuDisplay(VanillaStonecuttingDisplay.TITLE.value(), VanillaStonecuttingDisplay.LAYOUT.value(), VanillaStonecuttingDisplay.ICONS.value());
        }
        VanillaStonecuttingDisplayMenu vanillaStonecuttingDisplayMenu = new VanillaStonecuttingDisplayMenu(player, vanillaStonecuttingDisplay, recipe);
        vanillaStonecuttingDisplayMenu.openMenu();
        return vanillaStonecuttingDisplayMenu;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        this.vanillaStonecuttingDisplay = loadMenuDisplay(VanillaStonecuttingDisplay.TITLE.value(), VanillaStonecuttingDisplay.LAYOUT.value(), VanillaStonecuttingDisplay.ICONS.value());
    }

}
