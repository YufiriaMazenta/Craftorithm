package pers.yufiria.craftorithm.ui.creator.vanillaSmelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingSmokerCreatorConfig;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;

public class VanillaSmeltingSmokerCreator extends AbstractSmeltingCreator {

    public VanillaSmeltingSmokerCreator(@NotNull Player player, @Nullable String recipeName) {
        super(player, recipeName);
    }

    @Override
    protected StringConfig title() {
        return VanillaSmeltingSmokerCreatorConfig.TITLE;
    }

    @Override
    protected ConfigSectionConfig frameIconConfig() {
        return VanillaSmeltingSmokerCreatorConfig.FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig resultFrameIconConfig() {
        return VanillaSmeltingSmokerCreatorConfig.RESULT_FRAME_ICON;
    }

    @Override
    protected ConfigSectionConfig confirmIconConfig() {
        return VanillaSmeltingSmokerCreatorConfig.CONFIRM_ICON;
    }

    @Override
    protected ConfigSectionConfig expIconConfig() {
        return VanillaSmeltingSmokerCreatorConfig.EXP_ICON;
    }

    @Override
    protected ConfigSectionConfig timeIconConfig() {
        return VanillaSmeltingSmokerCreatorConfig.TIME_ICON;
    }

    @Override
    protected ConfigSectionConfig categoryIconFoodConfig() {
        return VanillaSmeltingSmokerCreatorConfig.CATEGORY_ICON_FOOD;
    }

    @Override
    protected ConfigSectionConfig categoryIconBlocksConfig() {
        return VanillaSmeltingSmokerCreatorConfig.CATEGORY_ICON_BLOCKS;
    }

    @Override
    protected ConfigSectionConfig categoryIconMiscConfig() {
        return VanillaSmeltingSmokerCreatorConfig.CATEGORY_ICON_MISC;
    }

    @Override
    protected int defaultExp() {
        return VanillaSmeltingSmokerCreatorConfig.DEFAULT_EXP.value();
    }

    @Override
    protected int defaultTime() {
        return VanillaSmeltingSmokerCreatorConfig.DEFAULT_TIME.value();
    }

    @Override
    protected SimpleRecipeTypes recipeType() {
        return SimpleRecipeTypes.VANILLA_SMELTING_SMOKER;
    }

}
