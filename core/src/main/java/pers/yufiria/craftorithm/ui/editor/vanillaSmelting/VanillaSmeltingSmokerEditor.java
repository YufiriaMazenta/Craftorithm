package pers.yufiria.craftorithm.ui.editor.vanillaSmelting;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.CookingRecipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingSmokerEditorConfig;

public final class VanillaSmeltingSmokerEditor extends BaseSmeltingEditor {
    public VanillaSmeltingSmokerEditor(@NotNull Player player, @NotNull NamespacedKey recipeKey, @NotNull CookingRecipe<?> recipe) {
        super(player, recipeKey, recipe);
    }
    @Override protected StringConfig title() { return VanillaSmeltingSmokerEditorConfig.TITLE; }
    @Override protected ConfigSectionConfig frameIconConfig() { return VanillaSmeltingSmokerEditorConfig.FRAME_ICON; }
    @Override protected ConfigSectionConfig resultFrameIconConfig() { return VanillaSmeltingSmokerEditorConfig.RESULT_FRAME_ICON; }
    @Override protected ConfigSectionConfig confirmIconConfig() { return VanillaSmeltingSmokerEditorConfig.CONFIRM_ICON; }
    @Override protected ConfigSectionConfig expIconConfig() { return VanillaSmeltingSmokerEditorConfig.EXP_ICON; }
    @Override protected ConfigSectionConfig timeIconConfig() { return VanillaSmeltingSmokerEditorConfig.TIME_ICON; }
}
