package pers.yufiria.craftorithm.ui.custom;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.BackableMenu;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Optional;
import java.util.function.BiFunction;

public class RecipeDisplayIcon extends ActionIcon {

    private final NamespacedKey recipeKey;
    private final Recipe recipe;
    private final RecipeType recipeType;

    public RecipeDisplayIcon(NamespacedKey recipeKey) {
        super(new IconDisplay(Material.AIR), null);
        this.recipeKey = recipeKey;
        Recipe recipe = RecipeManager.INSTANCE.getRecipe(recipeKey);
        this.recipe = recipe;
        this.recipeType = RecipeManager.INSTANCE.getRecipeType(recipe);
    }

    @Override
    public ItemStack display() {
        if (recipe != null) {
            return recipe.getResult().clone();
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        if (recipe == null) {
            return this;
        }
        Player whoClicked = ((Player) event.getWhoClicked());
        Optional<BiFunction<Player, Recipe, Menu>> recipeDisplayOpt = RecipeDisplayManager.INSTANCE.getRecipeDisplay(recipeType);
        recipeDisplayOpt.ifPresentOrElse(displayFunc -> {
            Optional<Menu> openingMenuOpt = MenuHelper.getOpeningMenu(whoClicked);
            Menu openingMenu = openingMenuOpt.orElse(null);
            Menu willOpenMenu = displayFunc.apply(whoClicked, recipe);
            if (willOpenMenu instanceof BackableMenu backableMenu) {
                backableMenu.setParentMenu(openingMenu);
            }
        }, () -> {
            LangUtils.sendLang(whoClicked, Languages.COMMAND_DISPLAY_UNSUPPORTED_RECIPE_TYPE);
        });
        return this;
    }

}
