package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingBlast;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingCampfire;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingFurnace;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingSmoker;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;
import pers.yufiria.craftorithm.ui.MenuManager;
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
public enum VanillaSmeltingDisplayMenuManager implements MenuManager<CookingRecipe<?>>, BukkitLifeCycleTask {

    INSTANCE;
    private final Map<RecipeType, MenuDisplay> displays = new ConcurrentHashMap<>();

    @Override
    public IconParser iconParser() {
        return VanillaSmeltingDisplayIconParser.INSTANCE;
    }

    @Override
    public void openMenu(Player player, CookingRecipe<?> recipe) {
        RecipeType recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
        MenuDisplay menuDisplay = displays.get(recipeType);
        if (menuDisplay == null) {
            LangUtils.sendLang(player, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
            return;
        }
        new VanillaSmeltingDisplayMenu(player, menuDisplay, recipe).openMenu();
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        MenuDisplay furnaceDisplay = loadMenuDisplay(VanillaSmeltingFurnace.TITLE.value(), VanillaSmeltingFurnace.LAYOUT.value(), VanillaSmeltingFurnace.ICONS.value());
        MenuDisplay blastDisplay = loadMenuDisplay(VanillaSmeltingBlast.TITLE.value(), VanillaSmeltingBlast.LAYOUT.value(), VanillaSmeltingBlast.ICONS.value());
        MenuDisplay campfireDisplay = loadMenuDisplay(VanillaSmeltingCampfire.TITLE.value(), VanillaSmeltingCampfire.LAYOUT.value(), VanillaSmeltingCampfire.ICONS.value());
        MenuDisplay smokerDisplay = loadMenuDisplay(VanillaSmeltingSmoker.TITLE.value(), VanillaSmeltingSmoker.LAYOUT.value(), VanillaSmeltingSmoker.ICONS.value());
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_FURNACE, furnaceDisplay);
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_BLAST, blastDisplay);
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE, campfireDisplay);
        displays.put(SimpleRecipeTypes.VANILLA_SMELTING_SMOKER, smokerDisplay);
    }
}
