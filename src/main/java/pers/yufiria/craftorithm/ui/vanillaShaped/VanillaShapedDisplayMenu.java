package pers.yufiria.craftorithm.ui.vanillaShaped;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

public class VanillaShapedDisplayMenu extends Menu {

    private final ShapedRecipe shapedRecipe;

    public VanillaShapedDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, ShapedRecipe shapedRecipe) {
        super(player, display);
        this.shapedRecipe = shapedRecipe;
    }

    @Override
    public String formattedTitle() {
        String originTitle = this.display.title();
        Player player = this.player();
        String title = LangManager.INSTANCE.replaceLang(originTitle, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, title));
    }

    @Override
    public void preProcessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        switch (icon) {
            case VanillaShapedIngredientIcon vanillaShapedIngredientIcon -> {
                int ingredientSlot = vanillaShapedIngredientIcon.ingredientSlot();
                int row = ingredientSlot / 3;
                int column = ingredientSlot % 3;
                @NotNull String[] shape = shapedRecipe.getShape();
                if (row >= shape.length) return;
                String line = shape[row];
                if (column >= line.length()) return;
                char c = line.charAt(column);
                RecipeChoice recipeChoice = shapedRecipe.getChoiceMap().get(c);
                if (recipeChoice == null) return;
                vanillaShapedIngredientIcon.setDisplayItem(recipeChoice.getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(shapedRecipe.getResult());
            }
            default -> {}
        }
    }

}
