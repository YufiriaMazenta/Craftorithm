package pers.yufiria.craftorithm.ui.display.vanillashapeless;

import crypticlib.ui.display.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaShapelessDisplay;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.List;
import java.util.Optional;

public class VanillaShapelessDisplayMenu extends RecipeDisplayMenu<ShapelessRecipe> {

    public VanillaShapelessDisplayMenu(@NotNull Player player, ShapelessRecipe recipe) {
        super(player, recipe);
        setDisplay(loadMenuDisplay(
            VanillaShapelessDisplay.TITLE.value(),
            VanillaShapelessDisplay.LAYOUT.value(),
            VanillaShapelessDisplay.ICONS.value()
        ));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ItemDisplayIcon itemDisplayIcon) {
            Optional<Object> iconTypeOpt = itemDisplayIcon.getData("icon_type");
            iconTypeOpt.ifPresent(obj -> {
                String iconType = obj.toString();
                switch (iconType.toLowerCase()) {
                    case VanillaShapelessDisplayIconParser.ICON_TYPE_INGREDIENT -> {
                        Optional<Object> slotOpt = itemDisplayIcon.getData("ingredient_slot");
                        if (slotOpt.isEmpty()) return;
                        int ingredientSlot = (int) slotOpt.get();
                        List<RecipeChoice> choiceList = recipe.getChoiceList();
                        if (ingredientSlot >= choiceList.size()) return;
                        RecipeChoice recipeChoice = choiceList.get(ingredientSlot);
                        if (recipeChoice != null) {
                            itemDisplayIcon.setDisplayItem(recipeChoice.getItemStack());
                        } else {
                            itemDisplayIcon.setDisplayItem(new ItemStack(Material.AIR));
                        }
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
        return VanillaShapelessDisplayIconParser.INSTANCE;
    }
}
