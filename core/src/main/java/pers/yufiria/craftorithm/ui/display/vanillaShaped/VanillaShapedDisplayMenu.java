package pers.yufiria.craftorithm.ui.display.vanillaShaped;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaShapedDisplay;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Optional;

public class VanillaShapedDisplayMenu extends RecipeDisplayMenu<ShapedRecipe> {

    public VanillaShapedDisplayMenu(@NotNull Player player, ShapedRecipe shapedRecipe) {
        super(player, shapedRecipe);
        setDisplay(loadMenuDisplay(
            VanillaShapedDisplay.TITLE.value(),
            VanillaShapedDisplay.LAYOUT.value(),
            VanillaShapedDisplay.ICONS.value()
        ));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ItemDisplayIcon itemDisplayIcon) {
            Optional<Object> iconTypeOpt = itemDisplayIcon.getData("icon_type");
            iconTypeOpt.ifPresent(obj -> {
                String iconType = obj.toString();
                switch (iconType.toLowerCase()) {
                    case VanillaShapedDisplayIconParser.ICON_TYPE_INGREDIENT -> {
                        Optional<Object> slotOpt = itemDisplayIcon.getData("ingredient_slot");
                        if (slotOpt.isEmpty()) return;
                        int ingredientSlot = (int) slotOpt.get();
                        int row = ingredientSlot / 3;
                        int column = ingredientSlot % 3;
                        @NotNull String[] shape = recipe.getShape();
                        if (row >= shape.length) return;
                        String line = shape[row];
                        if (column >= line.length()) return;
                        char c = line.charAt(column);
                        RecipeChoice recipeChoice = recipe.getChoiceMap().get(c);
                        if (recipeChoice == null) return;
                        itemDisplayIcon.setDisplayItem(recipeChoice.getItemStack());
                    }
                    case RecipeDisplayManager.ICON_TYPE_RESULT -> {
                        itemDisplayIcon.setDisplayItem(recipe.getResult());
                    }
                }
            });
        }
    }

    @Override
    public IconParser iconParser() {
        return VanillaShapedDisplayIconParser.INSTANCE;
    }

}
