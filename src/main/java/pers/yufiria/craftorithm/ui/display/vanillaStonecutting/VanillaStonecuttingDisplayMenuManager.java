package pers.yufiria.craftorithm.ui.display.vanillaStonecutting;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaStonecutting;
import pers.yufiria.craftorithm.ui.MenuManager;
import pers.yufiria.craftorithm.ui.display.vanillaStonecutting.VanillaStonecuttingDisplayIconParser;
import pers.yufiria.craftorithm.ui.display.vanillaStonecutting.VanillaStonecuttingDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE),
    @TaskRule(lifeCycle = LifeCycle.RELOAD)
})
public enum VanillaStonecuttingDisplayMenuManager implements MenuManager<StonecuttingRecipe>, BukkitLifeCycleTask {

    INSTANCE;
    private MenuDisplay vanillaStonecuttingDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaStonecuttingDisplayIconParser.INSTANCE;
    }

    @Override
    public void openMenu(Player player, StonecuttingRecipe recipe) {
        if (this.vanillaStonecuttingDisplay == null) {
            this.vanillaStonecuttingDisplay = loadMenuDisplay(VanillaStonecutting.TITLE.value(), VanillaStonecutting.LAYOUT.value(), VanillaStonecutting.ICONS.value());
        }
        new VanillaStonecuttingDisplayMenu(player, vanillaStonecuttingDisplay, recipe).openMenu();
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        this.vanillaStonecuttingDisplay = loadMenuDisplay(VanillaStonecutting.TITLE.value(), VanillaStonecutting.LAYOUT.value(), VanillaStonecutting.ICONS.value());
    }

}
