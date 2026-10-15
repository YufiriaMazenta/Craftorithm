package pers.yufiria.craftorithm.ui.display.vanillasmithing;

import crypticlib.MinecraftVersion;
import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmithingTransformDisplayConfig;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;

public class VanillaSmithingDisplayMenu extends RecipeDisplayMenu<SmithingRecipe> {

    public VanillaSmithingDisplayMenu(@NotNull Player player, SmithingRecipe recipe) {
        super(player, recipe);
        setDisplay(
            loadMenuDisplay(
                VanillaSmithingTransformDisplayConfig.TITLE.value(),
                VanillaSmithingTransformDisplayConfig.LAYOUT.value(),
                VanillaSmithingTransformDisplayConfig.ICONS.value()
            )
        );
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        applyIconType(icon, Map.of(
            VanillaSmithingDisplayIconParser.ICON_TYPE_TEMPLATE, this::showTemplate,
            VanillaSmithingDisplayIconParser.ICON_TYPE_BASE,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.getBase().getItemStack()),
            VanillaSmithingDisplayIconParser.ICON_TYPE_ADDITION,
            itemDisplayIcon -> itemDisplayIcon.setDisplayItem(recipe.getAddition().getItemStack())
        ));
    }

    /**
     * 显示锻造模板, 1.20以下不支持模板, 显示为空
     */
    private void showTemplate(ItemDisplayIcon itemDisplayIcon) {
        if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
            itemDisplayIcon.setDisplayItem(null);
            return;
        }
        if (recipe instanceof SmithingTransformRecipe transformRecipe) {
            itemDisplayIcon.setDisplayItem(transformRecipe.getTemplate().getItemStack());
        } else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
            itemDisplayIcon.setDisplayItem(trimRecipe.getTemplate().getItemStack());
        } else {
            itemDisplayIcon.setDisplayItem(null);
        }
    }

    @Override
    public IconParser iconParser() {
        return VanillaSmithingDisplayIconParser.INSTANCE;
    }
}
