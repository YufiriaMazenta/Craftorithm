package pers.yufiria.craftorithm.ui.display.vanillaSmithing;

import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitTextProcessor;
import crypticlib.lang.LangManager;
import crypticlib.ui.display.Icon;
import crypticlib.ui.display.MenuDisplay;
import crypticlib.ui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.ui.icon.RecipeResultIcon;

public class VanillaSmithingDisplayMenu extends Menu {

    private final SmithingRecipe recipe;

    public VanillaSmithingDisplayMenu(@NotNull Player player, @NotNull MenuDisplay display, SmithingRecipe recipe) {
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
            case VanillaSmithingTemplateIcon templateIcon -> {
                if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_20)) {
                    if (recipe instanceof SmithingTransformRecipe transformRecipe) {
                        templateIcon.setDisplayItem(transformRecipe.getTemplate().getItemStack());
                    } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
                        templateIcon.setDisplayItem(trimRecipe.getTemplate().getItemStack());
                    } else {
                        templateIcon.setDisplayItem(null);
                    }
                } else {
                    templateIcon.setDisplayItem(null);
                }
            }
            case VanillaSmithingBaseIcon vanillaSmithingBaseIcon -> {
                vanillaSmithingBaseIcon.setDisplayItem(recipe.getBase().getItemStack());
            }
            case VanillaSmithingAdditionIcon vanillaSmithingAdditionIcon -> {
                vanillaSmithingAdditionIcon.setDisplayItem(recipe.getAddition().getItemStack());
            }
            case RecipeResultIcon recipeResultIcon -> {
                recipeResultIcon.setDisplayItem(recipe.getResult());
            }
            default -> {}
        }
    }

}
