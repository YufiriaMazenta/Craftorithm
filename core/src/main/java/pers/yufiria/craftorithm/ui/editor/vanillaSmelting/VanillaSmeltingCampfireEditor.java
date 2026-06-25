package pers.yufiria.craftorithm.ui.editor.vanillaSmelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingCampfireEditorConfig;

public final class VanillaSmeltingCampfireEditor extends BaseSmeltingEditor {
    public VanillaSmeltingCampfireEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull CookingRecipe<?> recipe) {
        super(player, recipeKey, recipe);
    }
    @Override protected StringConfig title() { return VanillaSmeltingCampfireEditorConfig.TITLE; }
    @Override protected ConfigSectionConfig frameIconConfig() { return VanillaSmeltingCampfireEditorConfig.FRAME_ICON; }
    @Override protected ConfigSectionConfig resultFrameIconConfig() { return VanillaSmeltingCampfireEditorConfig.RESULT_FRAME_ICON; }
    @Override protected ConfigSectionConfig confirmIconConfig() { return VanillaSmeltingCampfireEditorConfig.CONFIRM_ICON; }
    @Override protected ConfigSectionConfig expIconConfig() { return VanillaSmeltingCampfireEditorConfig.EXP_ICON; }
    @Override protected ConfigSectionConfig timeIconConfig() { return VanillaSmeltingCampfireEditorConfig.TIME_ICON; }
    @Override protected ConfigSectionConfig getBackIconConfig() { return VanillaSmeltingCampfireEditorConfig.BACK_ICON; }
    @Override protected ConfigSectionConfig categoryIconFoodConfig() { return VanillaSmeltingCampfireEditorConfig.CATEGORY_ICON_FOOD; }
    @Override protected ConfigSectionConfig categoryIconBlocksConfig() { return VanillaSmeltingCampfireEditorConfig.CATEGORY_ICON_BLOCKS; }
    @Override protected ConfigSectionConfig categoryIconMiscConfig() { return VanillaSmeltingCampfireEditorConfig.CATEGORY_ICON_MISC; }
}
