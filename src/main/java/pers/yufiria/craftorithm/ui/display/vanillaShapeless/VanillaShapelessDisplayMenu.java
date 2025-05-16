package pers.yufiria.craftorithm.ui.display.vanillaShapeless;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.List;

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
                List<RecipeChoice> choiceList = recipe.getChoiceList();
                if (ingredientSlot >= choiceList.size()) {
                    return;
                }
                RecipeChoice recipeChoice = choiceList.get(ingredientSlot);
                if (recipeChoice != null) {
                    vanillaShapelessIngredientIcon.setDisplayItem(recipeChoice.getItemStack());
                } else {
                    vanillaShapelessIngredientIcon.setDisplayItem(new ItemStack(Material.AIR));
                }
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
