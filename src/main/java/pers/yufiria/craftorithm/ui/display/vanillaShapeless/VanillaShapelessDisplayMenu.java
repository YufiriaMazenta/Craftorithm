package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

public class VanillaShapelessDisplayMenu extends Menu {

    private final ShapelessRecipe recipe;

    public VanillaShapelessDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, ShapelessRecipe recipe) {
        super(player, display);
        this.recipe = recipe;
    }

    @Override
    public String parsedMenuTitle() {
        String originTitle = this.display.title();
        Player player = this.player();
        String title = LangManager.INSTANCE.replaceLang(originTitle, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case VanillaShapelessIngredientIcon vanillaShapelessIngredientIcon -> {
                int ingredientSlot = vanillaShapelessIngredientIcon.ingredientSlot();
//
//                if (recipeChoice == null) return;
//                vanillaShapelessIngredientIcon.setDisplayItem(recipeChoice.getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
