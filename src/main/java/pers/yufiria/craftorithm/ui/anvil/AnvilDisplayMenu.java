package pers.yufiria.craftorithm.ui.anvil;

import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipe;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

import java.util.Map;

public class AnvilDisplayMenu extends Menu {

    private final AnvilRecipe anvilRecipe;

    public AnvilDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, AnvilRecipe anvilRecipe) {
        super(player, display);
        this.anvilRecipe = anvilRecipe;
    }

    @Override
    public String parsedMenuTitle() {
        String originTitle = this.display.title();
        Player player = this.player();
        String title = LangManager.INSTANCE.replaceLang(originTitle, player);
        return BukkitTextProcessor.color(BukkitTextProcessor.placeholder(player, replaceCostLevel(title)));
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ActionIcon actionIcon) {
            actionIcon.setTextReplaceMap(Map.of("<level>", anvilRecipe.costLevel() + ""));
        }
        switch (icon) {
            case AnvilBaseIcon anvilBaseIcon -> {
                anvilBaseIcon.setDisplayItem(anvilRecipe.base().getItemStack());
            }
            case AnvilAdditionIcon anvilAdditionIcon -> {
                anvilAdditionIcon.setDisplayItem(anvilRecipe.addition().getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(anvilRecipe.getResult());
            }
            default -> {}
        }
    }

    private String replaceCostLevel(String originText) {
        return originText.replace("<level>", anvilRecipe.costLevel() + "");
    }

}
