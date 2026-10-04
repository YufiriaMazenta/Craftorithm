package pers.yufiria.craftorithm.ui.display.vanillasmelting;

import crypticlib.ui.display.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingBlastDisplayConfig;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingCampfireDisplayConfig;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingFurnaceDisplayConfig;
import pers.yufiria.craftorithm.config.menu.display.VanillaSmeltingSmokerDisplayConfig;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayManager;
import pers.yufiria.craftorithm.ui.display.RecipeDisplayMenu;
import pers.yufiria.craftorithm.ui.icon.ActionIcon;
import pers.yufiria.craftorithm.ui.icon.IconParser;
import pers.yufiria.craftorithm.ui.icon.ItemDisplayIcon;

import java.util.Map;
import java.util.Optional;

public class VanillaSmeltingDisplayMenu extends RecipeDisplayMenu<CookingRecipe<?>> {

    public VanillaSmeltingDisplayMenu(@NotNull Player player, CookingRecipe<?> recipe) {
        super(player, recipe);
        switch (recipe) {
            case FurnaceRecipe furnaceRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingFurnaceDisplayConfig.TITLE.value(),
                        VanillaSmeltingFurnaceDisplayConfig.LAYOUT.value(),
                        VanillaSmeltingFurnaceDisplayConfig.ICONS.value()
                    )
                );
            }
            case BlastingRecipe blastingRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingBlastDisplayConfig.TITLE.value(),
                        VanillaSmeltingBlastDisplayConfig.LAYOUT.value(),
                        VanillaSmeltingBlastDisplayConfig.ICONS.value()
                    )
                );
            }
            case SmokingRecipe smokingRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingSmokerDisplayConfig.TITLE.value(),
                        VanillaSmeltingSmokerDisplayConfig.LAYOUT.value(),
                        VanillaSmeltingSmokerDisplayConfig.ICONS.value()
                    )
                );
            }
            case CampfireRecipe campfireRecipe -> {
                setDisplay(
                    loadMenuDisplay(
                        VanillaSmeltingCampfireDisplayConfig.TITLE.value(),
                        VanillaSmeltingCampfireDisplayConfig.LAYOUT.value(),
                        VanillaSmeltingCampfireDisplayConfig.ICONS.value()
                    )
                );
            }
            default -> throw new IllegalStateException("Unexpected value: " + recipe);
        }
    }

    @Override
    public String parsedMenuTitle() {
        return replaceExpAndTime(super.parsedMenuTitle());
    }

    @Override
    public void preprocessIconWhenUpdateLayout(Integer slot, @NotNull Icon icon) {
        if (icon instanceof ActionIcon actionIcon) {
            actionIcon.setTextReplaceMap(Map.of("<reward_exp>", recipe.getExperience() + "", "<time>", recipe.getCookingTime() + ""));
        }
        if (icon instanceof ItemDisplayIcon itemDisplayIcon) {
            Optional<Object> iconTypeOpt = itemDisplayIcon.getData("icon_type");
            iconTypeOpt.ifPresent(obj -> {
                String iconType = obj.toString();
                switch (iconType.toLowerCase()) {
                    case VanillaSmeltingDisplayIconParser.ICON_TYPE_INGREDIENT -> {
                        itemDisplayIcon.setDisplayItem(recipe.getInputChoice().getItemStack());
                    }
                    case RecipeDisplayManager.ICON_TYPE_RESULT -> {
                        itemDisplayIcon.setDisplayItem(recipe.getResult());
                    }
                }
            });
        }
    }

    public String replaceExpAndTime(String origin) {
        return origin.replace("<reward_exp>", recipe.getExperience() + "").replace("<time>", recipe.getCookingTime() + "");
    }

    @Override
    public IconParser iconParser() {
        return VanillaSmeltingDisplayIconParser.INSTANCE;
    }
}
