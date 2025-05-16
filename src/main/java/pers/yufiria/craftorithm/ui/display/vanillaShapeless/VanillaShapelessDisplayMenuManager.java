package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaShapeless;
import pers.yufiria.craftorithm.ui.MenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum VanillaShapelessDisplayMenuManager implements MenuManager<ShapelessRecipe>, BukkitLifeCycleTask {
    
    INSTANCE;

    private MenuDisplay vanillaShapelessDisplayMenuDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaShapelessDisplayIconParser.INSTANCE;
    }

    @Override
    public void openMenu(Player player, ShapelessRecipe recipe) {
        if (vanillaShapelessDisplayMenuDisplay == null) {
            vanillaShapelessDisplayMenuDisplay = loadMenuDisplay(VanillaShapeless.TITLE.value(), VanillaShapeless.LAYOUT.value(), VanillaShapeless.ICONS.value());
        }
        new VanillaShapelessDisplayMenu(player, vanillaShapelessDisplayMenuDisplay, recipe).openMenu();
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        vanillaShapelessDisplayMenuDisplay = loadMenuDisplay(VanillaShapeless.TITLE.value(), VanillaShapeless.LAYOUT.value(), VanillaShapeless.ICONS.value());
    }
    
}
