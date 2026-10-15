package pers.yufiria.craftorithm.ui.display.anvil;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.AnvilDisplayConfig;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;

import java.util.Map;

public class AnvilDisplayMenu extends RecipeDisplayMenu<AnvilRecipe> {

    public AnvilDisplayMenu(@NotNull Player player, AnvilRecipe anvilRecipe) {
        super(
            player,
            anvilRecipe
        );
        setDisplay(loadMenuDisplay(
            AnvilDisplayConfig.TITLE.value(),
            AnvilDisplayConfig.LAYOUT.value(),
            AnvilDisplayConfig.ICONS.value()
        ));
    }

    @Override
    public String parsedMenuTitle() {
        return replaceCostLevel(super.parsedMenuTitle());
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ActionIcon actionIcon) {
            actionIcon.setTextReplaceMap(Map.of("<level>", recipe.costLevel() + ""));
        }
        applyIconType(icon, Map.of(
            AnvilDisplayIconParser.ICON_TYPE_BASE,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.base().getItemStack()),
            AnvilDisplayIconParser.ICON_TYPE_ADDITION,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.addition().getItemStack())
        ));
    }

    private String replaceCostLevel(String originText) {
        return originText.replace("<level>", recipe.costLevel() + "");
    }

    @Override
    public IconParser iconParser() {
        return AnvilDisplayIconParser.INSTANCE;
    }

}
