package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaShapelessDisplay;
import pers.yufiria.craftorithm.ui.RecipeDisplayMenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum VanillaShapelessDisplayMenuManager implements RecipeDisplayMenuManager<ShapelessRecipe>, BukkitLifeCycleTask {
    
    INSTANCE;

    private MenuDisplay vanillaShapelessDisplayMenuDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaShapelessDisplayIconParser.INSTANCE;
    }

    @Override
    public Menu openMenu(Player player, ShapelessRecipe recipe) {
        if (vanillaShapelessDisplayMenuDisplay == null) {
            vanillaShapelessDisplayMenuDisplay = loadMenuDisplay(VanillaShapelessDisplay.TITLE.value(), VanillaShapelessDisplay.LAYOUT.value(), VanillaShapelessDisplay.ICONS.value());
        }
        VanillaShapelessDisplayMenu vanillaShapelessDisplayMenu = new VanillaShapelessDisplayMenu(player, vanillaShapelessDisplayMenuDisplay, recipe);
        vanillaShapelessDisplayMenu.openMenu();
        return vanillaShapelessDisplayMenu;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        vanillaShapelessDisplayMenuDisplay = loadMenuDisplay(VanillaShapelessDisplay.TITLE.value(), VanillaShapelessDisplay.LAYOUT.value(), VanillaShapelessDisplay.ICONS.value());
    }
    
}
