package pers.yufiria.craftorithm.ui.recipeBook;

import crypticlib.ui.display.Icon;
import crypticlib.ui.display.IconDisplay;
import crypticlib.ui.menu.Menu;
import crypticlib.ui.util.MenuHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.TranslatableIcon;

import java.util.Objects;
import java.util.Optional;

class SortIcon extends TranslatableIcon {

    private SortMode sortMode = null;

    public SortIcon(IconDisplay iconDisplay) {
        super(iconDisplay);
    }

    @Override
    public String parseIconText(String originText) {
        Player iconParsePlayer = this.parsePlayer();
        return super.parseIconText(originText.replace(
            "<sort_mode>",
            sortMode != null ? sortMode.nameLang().value(Objects.requireNonNull(iconParsePlayer)) : ""
        ));
    }

    @Override
    public Icon onClick(InventoryClickEvent event) {
        Optional<Menu> openingMenu = MenuHelper.getOpeningMenu((Player) event.getWhoClicked());
        openingMenu.ifPresent((menu -> {
            if (menu instanceof RecipeListMenu recipeListMenu) {
                recipeListMenu.setSortMode(sortMode.next());
                recipeListMenu.updateMenu();
            }
        }));
        return this;
    }

    public SortMode sortMode() {
        return sortMode;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
    }

}
