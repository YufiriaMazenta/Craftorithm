package pers.yufiria.craftorithm.ui.display.vanillashapeless;

import crypticlib.ui.display.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaShapelessDisplayConfig;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VanillaShapelessDisplayMenu extends RecipeDisplayMenu<ShapelessRecipe> {

    public VanillaShapelessDisplayMenu(@NotNull Player player, ShapelessRecipe recipe) {
        super(player, recipe);
        setDisplay(loadMenuDisplay(
            VanillaShapelessDisplayConfig.TITLE.value(),
            VanillaShapelessDisplayConfig.LAYOUT.value(),
            VanillaShapelessDisplayConfig.ICONS.value()
        ));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        applyIconType(icon, Map.of(
            VanillaShapelessDisplayIconParser.ICON_TYPE_INGREDIENT, this::showIngredient
        ));
    }

    /**
     * 按材料序号显示对应的材料, 序号越界时不做处理
     */
    private void showIngredient(ItemDisplayIcon itemDisplayIcon) {
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

    @Override
    public IconParser iconParser() {
        return VanillaShapelessDisplayIconParser.INSTANCE;
    }
}
