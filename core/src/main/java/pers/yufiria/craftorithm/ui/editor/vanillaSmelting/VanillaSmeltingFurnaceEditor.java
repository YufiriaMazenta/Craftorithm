package pers.yufiria.craftorithm.ui.editor.vanillaSmelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingFurnaceEditorConfig;

public final class VanillaSmeltingFurnaceEditor extends BaseSmeltingEditor {
    public VanillaSmeltingFurnaceEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull CookingRecipe<?> recipe) {
        super(player, recipeKey, recipe);
    }
    @Override protected StringConfig title() { return VanillaSmeltingFurnaceEditorConfig.TITLE; }
    @Override protected ConfigSectionConfig frameIconConfig() { return VanillaSmeltingFurnaceEditorConfig.FRAME_ICON; }
    @Override protected ConfigSectionConfig resultFrameIconConfig() { return VanillaSmeltingFurnaceEditorConfig.RESULT_FRAME_ICON; }
    @Override protected ConfigSectionConfig confirmIconConfig() { return VanillaSmeltingFurnaceEditorConfig.CONFIRM_ICON; }
    @Override protected ConfigSectionConfig expIconConfig() { return VanillaSmeltingFurnaceEditorConfig.EXP_ICON; }
    @Override protected ConfigSectionConfig timeIconConfig() { return VanillaSmeltingFurnaceEditorConfig.TIME_ICON; }
    @Override protected ConfigSectionConfig getBackIconConfig() { return VanillaSmeltingFurnaceEditorConfig.BACK_ICON; }
    @Override protected ConfigSectionConfig categoryIconFoodConfig() { return VanillaSmeltingFurnaceEditorConfig.CATEGORY_ICON_FOOD; }
    @Override protected ConfigSectionConfig categoryIconBlocksConfig() { return VanillaSmeltingFurnaceEditorConfig.CATEGORY_ICON_BLOCKS; }
    @Override protected ConfigSectionConfig categoryIconMiscConfig() { return VanillaSmeltingFurnaceEditorConfig.CATEGORY_ICON_MISC; }
}
