package pers.yufiria.craftorithm.ui.creator.smelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingFurnaceCreatorConfig;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;

public class VanillaSmeltingFurnaceCreator extends AbstractSmeltingCreator {

    public VanillaSmeltingFurnaceCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
    }

    @Override
    protected StringConfig title() {
        return VanillaSmeltingFurnaceCreatorConfig.TITLE;
    }

    @Override
    protected ConfigSectionConfig frameIconConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig resultFrameIconConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.RESULT_FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig confirmIconConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.CONFIRM_ICON;
    }

    @Override
    protected ConfigSectionConfig expIconConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.EXP_ICON;
    }

    @Override
    protected ConfigSectionConfig timeIconConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.TIME_ICON;
    }

    @Override
    protected ConfigSectionConfig categoryIconFoodConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.CATEGORY_ICON_FOOD;
    }

    @Override
    protected ConfigSectionConfig categoryIconBlocksConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.CATEGORY_ICON_BLOCKS;
    }

    @Override
    protected ConfigSectionConfig categoryIconMiscConfig() {
        return VanillaSmeltingFurnaceCreatorConfig.CATEGORY_ICON_MISC;
    }

    @Override
    protected int defaultExp() {
        return VanillaSmeltingFurnaceCreatorConfig.DEFAULT_EXP.value();
    }

    @Override
    protected int defaultTime() {
        return VanillaSmeltingFurnaceCreatorConfig.DEFAULT_TIME.value();
    }

    @Override
    protected SimpleRecipeTypes recipeType() {
        return SimpleRecipeTypes.VANILLA_SMELTING_FURNACE;
    }

}
