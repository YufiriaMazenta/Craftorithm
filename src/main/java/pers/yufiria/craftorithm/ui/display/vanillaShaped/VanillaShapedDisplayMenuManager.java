package pers.yufiria.craftorithm.ui.display.vanillaShaped;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaShaped;
import pers.yufiria.craftorithm.ui.RecipeDisplayMenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum VanillaShapedDisplayMenuManager implements RecipeDisplayMenuManager<ShapedRecipe>, BukkitLifeCycleTask {
    
    INSTANCE;

    private MenuDisplay vanillaShapedDisplayMenuDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaShapedDisplayIconParser.INSTANCE;
    }

    @Override
    public Menu openMenu(Player player, ShapedRecipe recipe) {
        if (vanillaShapedDisplayMenuDisplay == null) {
            vanillaShapedDisplayMenuDisplay = loadMenuDisplay(VanillaShaped.TITLE.value(), VanillaShaped.LAYOUT.value(), VanillaShaped.ICONS.value());
        }
        VanillaShapedDisplayMenu vanillaShapedDisplayMenu = new VanillaShapedDisplayMenu(player, vanillaShapedDisplayMenuDisplay, recipe);
        vanillaShapedDisplayMenu.openMenu();
        return vanillaShapedDisplayMenu;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        vanillaShapedDisplayMenuDisplay = loadMenuDisplay(VanillaShaped.TITLE.value(), VanillaShaped.LAYOUT.value(), VanillaShaped.ICONS.value());
    }
    
}
