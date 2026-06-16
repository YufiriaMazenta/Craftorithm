package pers.yufiria.craftorithm.ui.creator.smelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingCampfireCreatorConfig;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;

public class VanillaSmeltingCampfireCreator extends AbstractSmeltingCreator {

    public VanillaSmeltingCampfireCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
    }

    @Override
    protected StringConfig title() {
        return VanillaSmeltingCampfireCreatorConfig.TITLE;
    }

    @Override
    protected ConfigSectionConfig frameIconConfig() {
        return VanillaSmeltingCampfireCreatorConfig.FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig resultFrameIconConfig() {
        return VanillaSmeltingCampfireCreatorConfig.RESULT_FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig confirmIconConfig() {
        return VanillaSmeltingCampfireCreatorConfig.CONFIRM_ICON;
    }

    @Override
    protected ConfigSectionConfig expIconConfig() {
        return VanillaSmeltingCampfireCreatorConfig.EXP_ICON;
    }

    @Override
    protected ConfigSectionConfig timeIconConfig() {
        return VanillaSmeltingCampfireCreatorConfig.TIME_ICON;
    }

    @Override
    protected ConfigSectionConfig categoryIconFoodConfig() {
        return VanillaSmeltingCampfireCreatorConfig.CATEGORY_ICON_FOOD;
    }

    @Override
    protected ConfigSectionConfig categoryIconBlocksConfig() {
        return VanillaSmeltingCampfireCreatorConfig.CATEGORY_ICON_BLOCKS;
    }

    @Override
    protected ConfigSectionConfig categoryIconMiscConfig() {
        return VanillaSmeltingCampfireCreatorConfig.CATEGORY_ICON_MISC;
    }

    @Override
    protected int defaultExp() {
        return VanillaSmeltingCampfireCreatorConfig.DEFAULT_EXP.value();
    }

    @Override
    protected int defaultTime() {
        return VanillaSmeltingCampfireCreatorConfig.DEFAULT_TIME.value();
    }

    @Override
    protected SimpleRecipeTypes recipeType() {
        return SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE;
    }

}
