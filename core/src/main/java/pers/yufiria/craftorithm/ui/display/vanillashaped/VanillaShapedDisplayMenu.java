package pers.yufiria.craftorithm.ui.display.vanillashaped;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaShapedDisplayConfig;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.Optional;

public class VanillaShapedDisplayMenu extends RecipeDisplayMenu<ShapedRecipe> {

    public VanillaShapedDisplayMenu(@NotNull Player player, ShapedRecipe shapedRecipe) {
        super(player, shapedRecipe);
        setDisplay(loadMenuDisplay(
            VanillaShapedDisplayConfig.TITLE.value(),
            VanillaShapedDisplayConfig.LAYOUT.value(),
            VanillaShapedDisplayConfig.ICONS.value()
        ));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        applyIconType(icon, Map.of(
            VanillaShapedDisplayIconParser.ICON_TYPE_INGREDIENT, this::showIngredient
        ));
    }

    /**
     * 在3x3网格槽位上显示对应的材料
     */
    private void showIngredient(ItemDisplayIcon itemDisplayIcon) {
        Optional<Object> slotOpt = itemDisplayIcon.getData("ingredient_slot");
        if (slotOpt.isEmpty()) return;
        int ingredientSlot = (int) slotOpt.get();
        int row = ingredientSlot / 3;
        int column = ingredientSlot % 3;
        String[] shape = recipe.getShape();
        if (row >= shape.length) return;
        String line = shape[row];
        if (column >= line.length()) return;
        char c = line.charAt(column);
        RecipeChoice recipeChoice = recipe.getChoiceMap().get(c);
        if (recipeChoice == null) return;
        itemDisplayIcon.setDisplayItem(recipeChoice.getItemStack());
    }

    @Override
    public IconParser iconParser() {
        return VanillaShapedDisplayIconParser.INSTANCE;
    }

}
