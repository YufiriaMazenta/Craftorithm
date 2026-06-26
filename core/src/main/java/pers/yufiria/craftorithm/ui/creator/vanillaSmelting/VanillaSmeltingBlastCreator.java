package pers.yufiria.craftorithm.ui.creator.vanillaSmelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingBlastCreatorConfig;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;

public class VanillaSmeltingBlastCreator extends AbstractSmeltingCreator {

    public VanillaSmeltingBlastCreator(@NotNull Player player, @Nullable String recipeId, @Nullable String recipeFileName) {
        super(player, recipeId, recipeFileName);
    }

    @Override
    protected StringConfig title() {
        return VanillaSmeltingBlastCreatorConfig.TITLE;
    }

    @Override
    protected ConfigSectionConfig frameIconConfig() {
        return VanillaSmeltingBlastCreatorConfig.FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig resultFrameIconConfig() {
        return VanillaSmeltingBlastCreatorConfig.RESULT_FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig confirmIconConfig() {
        return VanillaSmeltingBlastCreatorConfig.CONFIRM_ICON;
    }

    @Override
    protected ConfigSectionConfig expIconConfig() {
        return VanillaSmeltingBlastCreatorConfig.EXP_ICON;
    }

    @Override
    protected ConfigSectionConfig timeIconConfig() {
        return VanillaSmeltingBlastCreatorConfig.TIME_ICON;
    }

    @Override
    protected ConfigSectionConfig categoryIconFoodConfig() {
        return VanillaSmeltingBlastCreatorConfig.CATEGORY_ICON_FOOD;
    }

    @Override
    protected ConfigSectionConfig categoryIconBlocksConfig() {
        return VanillaSmeltingBlastCreatorConfig.CATEGORY_ICON_BLOCKS;
    }

    @Override
    protected ConfigSectionConfig categoryIconMiscConfig() {
        return VanillaSmeltingBlastCreatorConfig.CATEGORY_ICON_MISC;
    }

    @Override
    protected int defaultExp() {
        return VanillaSmeltingBlastCreatorConfig.DEFAULT_EXP.value();
    }

    @Override
    protected int defaultTime() {
        return VanillaSmeltingBlastCreatorConfig.DEFAULT_TIME.value();
    }

    @Override
    protected SimpleRecipeTypes recipeType() {
        return SimpleRecipeTypes.VANILLA_SMELTING_BLAST;
    }

}
