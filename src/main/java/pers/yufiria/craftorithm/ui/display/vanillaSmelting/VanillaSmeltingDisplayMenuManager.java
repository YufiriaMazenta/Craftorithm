package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingBlastDisplay;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingCampfireDisplay;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingFurnaceDisplay;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingSmokerDisplay;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.RecipeDisplayMenuManager;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.RELOAD),
    }
)
public enum VanillaSmeltingDisplayMenuManager implements RecipeDisplayMenuManager<CookingRecipe<?>>, BukkitLifeCycleTask {

    INSTANCE;
    private final Map<RecipeType, MenuDisplay> displays = new ConcurrentHashMap<>();

    @Override
    public IconParser iconParser() {
        return VanillaSmeltingDisplayIconParser.INSTANCE;
    }

    @Override
    public Menu openMenu(Player player, CookingRecipe<?> recipe) {
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        MenuDisplay menuDisplay = displays.get(recipeType);
        if (menuDisplay == null) {
            LangUtils.sendLang(player, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
            return null;
        }
        VanillaSmeltingDisplayMenu vanillaSmeltingDisplayMenu = new VanillaSmeltingDisplayMenu(player, menuDisplay, recipe);
        vanillaSmeltingDisplayMenu.openMenu();
        return vanillaSmeltingDisplayMenu;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        MenuDisplay furnaceDisplay = loadMenuDisplay(VanillaSmeltingFurnaceDisplay.TITLE.value(), VanillaSmeltingFurnaceDisplay.LAYOUT.value(), VanillaSmeltingFurnaceDisplay.ICONS.value());
        MenuDisplay blastDisplay = loadMenuDisplay(VanillaSmeltingBlastDisplay.TITLE.value(), VanillaSmeltingBlastDisplay.LAYOUT.value(), VanillaSmeltingBlastDisplay.ICONS.value());
        MenuDisplay campfireDisplay = loadMenuDisplay(VanillaSmeltingCampfireDisplay.TITLE.value(), VanillaSmeltingCampfireDisplay.LAYOUT.value(), VanillaSmeltingCampfireDisplay.ICONS.value());
        MenuDisplay smokerDisplay = loadMenuDisplay(VanillaSmeltingSmokerDisplay.TITLE.value(), VanillaSmeltingSmokerDisplay.LAYOUT.value(), VanillaSmeltingSmokerDisplay.ICONS.value());
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, furnaceDisplay);
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, blastDisplay);
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, campfireDisplay);
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, smokerDisplay);
    }
}
