package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.script.compile.CompiledScript;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;

import java.util.Map;
import java.util.Optional;

public class RecipeListIcon extends ActionIcon {

    private final RecipeType recipeType;

    public RecipeListIcon(@NotNull IconDisplay iconDisplay, @NotNull RecipeType recipeType) {
        super(iconDisplay);
        this.recipeType = recipeType;
    }

    public RecipeListIcon(@NotNull IconDisplay iconDisplay, @NotNull RecipeType recipeType, @NotNull Map<ClickType, CompiledScript> actions) {
        super(iconDisplay, actions);
        this.recipeType = recipeType;
    }

    @Override
    public ItemStack display() {
        long count = RecipeManager.INSTANCE.getRecipesByType(recipeType).size();
        Map<String, String> rm = textReplaceMap();
        rm.put("<recipe_count>", String.valueOf(count));
        rm.put("<type_name>", recipeType.typeKey());
        setTextReplaceMap(rm);
        return super.display();
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Optional<Menu> currentMenuOpt = MenuHelper.getOpeningMenu(player);
        Menu parentMenu = currentMenuOpt.orElse(null);
        new RecipeListMenu(player, recipeType, SortMode.NAME_ASC, parentMenu).openMenu();
        runActions(event, this.actions);
        return this;
    }

    public RecipeType recipeType() {
        return recipeType;
    }

}
