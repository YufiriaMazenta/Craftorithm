package pers.yufiria.craftorithm.ui.display.vanillaSmelting;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.Map;

public class VanillaSmeltingDisplayMenu extends Menu {

    private final CookingRecipe<?> recipe;

    public VanillaSmeltingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, CookingRecipe<?> recipe) {
        super(player, display);
        this.recipe = recipe;
    }

    @Override
    public String parsedMenuTitle() {
        String originTitle = this.display.title();
        Player player = this.player();
        String title = LangManager.INSTANCE.replaceLang(originTitle, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, replaceExpAndTime(title)));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ActionIcon actionIcon) {
            actionIcon.setTextReplaceMap(Map.of("<reward_exp>", recipe.getExperience() + "", "<time>", recipe.getCookingTime() + ""));
        }
        switch (icon) {
            case VanillaSmeltingIngredientIcon ingredientIcon -> {
                ingredientIcon.setDisplayItem(recipe.getInputChoice().getItemStack());
            }
            case RecipeResultIcon resultIcon -> {
                resultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

    public String replaceExpAndTime(String origin) {
        return origin.replace("<reward_exp>", recipe.getExperience() + "").replace("<time>", recipe.getCookingTime() + "");
    }

}
