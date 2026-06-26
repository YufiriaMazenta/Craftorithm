package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.Map;
import java.util.Optional;

public class RecipeListIcon extends TranslatableIcon {

    private final RecipeType recipeType;

    public RecipeListIcon(@NotNull IconDisplay iconDisplay, @NotNull RecipeType recipeType) {
        super(iconDisplay);
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
        new RecipeBookListMenu(player, recipeType, 0, SortMode.NAME_ASC, parentMenu).openMenu();
        return this;
    }

    public RecipeType recipeType() {
        return recipeType;
    }

}
