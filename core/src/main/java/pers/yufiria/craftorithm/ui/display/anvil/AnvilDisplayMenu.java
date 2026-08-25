package pers.yufiria.craftorithm.ui.display.anvil;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.AnvilDisplay;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipe;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.Optional;

public class AnvilDisplayMenu extends RecipeDisplayMenu<AnvilRecipe> {

    public AnvilDisplayMenu(@NotNull Player player, AnvilRecipe anvilRecipe) {
        super(
            player,
            anvilRecipe
        );
        setDisplay(loadMenuDisplay(
            AnvilDisplay.TITLE.value(),
            AnvilDisplay.LAYOUT.value(),
            AnvilDisplay.ICONS.value()
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
        if (icon instanceof ItemDisplayIcon itemDisplayIcon) {
            Optional<Object> iconTypeOpt = itemDisplayIcon.getData("icon_type");
            iconTypeOpt.ifPresent(obj -> {
                String iconType = obj.toString();
                switch (iconType.toLowerCase()) {
                    case AnvilDisplayIconParser.ICON_TYPE_BASE -> {
                        itemDisplayIcon.setDisplayItem(recipe.base().getItemStack());
                    }
                    case AnvilDisplayIconParser.ICON_TYPE_ADDITION -> {
                        itemDisplayIcon.setDisplayItem(recipe.addition().getItemStack());
                    }
                    case RecipeDisplayManager.ICON_TYPE_RESULT -> {
                        itemDisplayIcon.setDisplayItem(recipe.getResult());
                    }
                }
            });
        }
    }

    private String replaceCostLevel(String originText) {
        return originText.replace("<level>", recipe.costLevel() + "");
    }

    @Override
    public IconParser iconParser() {
        return AnvilDisplayIconParser.INSTANCE;
    }

}
