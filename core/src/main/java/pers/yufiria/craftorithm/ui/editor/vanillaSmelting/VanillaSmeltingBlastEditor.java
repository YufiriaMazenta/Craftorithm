package pers.yufiria.craftorithm.ui.editor.vanillaSmelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingBlastEditorConfig;

public final class VanillaSmeltingBlastEditor extends BaseSmeltingEditor {
    public VanillaSmeltingBlastEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull CookingRecipe<?> recipe) {
        super(player, recipeKey, recipe);
    }
    @Override protected StringConfig title() { return VanillaSmeltingBlastEditorConfig.TITLE; }
    @Override protected ConfigSectionConfig frameIconConfig() { return VanillaSmeltingBlastEditorConfig.FRAME_ICON; }
    @Override protected ConfigSectionConfig resultFrameIconConfig() { return VanillaSmeltingBlastEditorConfig.RESULT_FRAME_ICON; }
    @Override protected ConfigSectionConfig confirmIconConfig() { return VanillaSmeltingBlastEditorConfig.CONFIRM_ICON; }
    @Override protected ConfigSectionConfig expIconConfig() { return VanillaSmeltingBlastEditorConfig.EXP_ICON; }
    @Override protected ConfigSectionConfig timeIconConfig() { return VanillaSmeltingBlastEditorConfig.TIME_ICON; }
    @Override protected ConfigSectionConfig getBackIconConfig() { return VanillaSmeltingBlastEditorConfig.BACK_ICON; }
    @Override protected ConfigSectionConfig categoryIconFoodConfig() { return VanillaSmeltingBlastEditorConfig.CATEGORY_ICON_FOOD; }
    @Override protected ConfigSectionConfig categoryIconBlocksConfig() { return VanillaSmeltingBlastEditorConfig.CATEGORY_ICON_BLOCKS; }
    @Override protected ConfigSectionConfig categoryIconMiscConfig() { return VanillaSmeltingBlastEditorConfig.CATEGORY_ICON_MISC; }
}
