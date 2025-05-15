package pers.yufiria.craftorithm.ui.display.vanillaShaped;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaShaped;
import pers.yufiria.craftorithm.ui.MenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum VanillaShapedDisplayMenuManager implements MenuManager<ShapedRecipe>, BukkitLifeCycleTask {
    
    INSTANCE;

    private MenuDisplay vanillaShapedDisplayMenuDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaShapedDisplayIconParser.INSTANCE;
    }

    @Override
    public void openMenu(Player player, ShapedRecipe recipe) {
        if (vanillaShapedDisplayMenuDisplay == null) {
            vanillaShapedDisplayMenuDisplay = loadMenuDisplay(VanillaShaped.TITLE.value(), VanillaShaped.LAYOUT.value(), VanillaShaped.ICONS.value());
        }
        new VanillaShapedDisplayMenu(player, vanillaShapedDisplayMenuDisplay, recipe).openMenu();
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        vanillaShapedDisplayMenuDisplay = loadMenuDisplay(VanillaShaped.TITLE.value(), VanillaShaped.LAYOUT.value(), VanillaShaped.ICONS.value());
    }
    
}
