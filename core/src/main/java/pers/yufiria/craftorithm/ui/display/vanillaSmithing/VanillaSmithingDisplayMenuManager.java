package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmithingDisplay;
import pers.yufiria.craftorithm.ui.RecipeDisplayMenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.ENABLE),
    @TaskRule(lifeCycle = LifeCycle.RELOAD)
})
public enum VanillaSmithingDisplayMenuManager implements RecipeDisplayMenuManager<SmithingRecipe>, BukkitLifeCycleTask {

    INSTANCE;
    private MenuDisplay vanillaSmithingDisplay;

    @Override
    public IconParser iconParser() {
        return VanillaSmithingDisplayIconParser.INSTANCE;
    }

    @Override
    public Menu openMenu(Player player, SmithingRecipe recipe) {
        if (this.vanillaSmithingDisplay == null) {
            this.vanillaSmithingDisplay = loadMenuDisplay(VanillaSmithingDisplay.TITLE.value(), VanillaSmithingDisplay.LAYOUT.value(), VanillaSmithingDisplay.ICONS.value());
        }
        VanillaSmithingDisplayMenu vanillaSmithingDisplayMenu = new VanillaSmithingDisplayMenu(player, vanillaSmithingDisplay, recipe);
        vanillaSmithingDisplayMenu.openMenu();
        return vanillaSmithingDisplayMenu;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        this.vanillaSmithingDisplay = loadMenuDisplay(VanillaSmithingDisplay.TITLE.value(), VanillaSmithingDisplay.LAYOUT.value(), VanillaSmithingDisplay.ICONS.value());
    }

}
