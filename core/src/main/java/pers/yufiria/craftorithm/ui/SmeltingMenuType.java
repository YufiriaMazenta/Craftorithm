package pers.yufiria.craftorithm.ui;

import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingBlastCreatorConfig;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingCampfireCreatorConfig;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingFurnaceCreatorConfig;
import pers.yufiria.craftorithm.config.menu.creator.VanillaSmeltingSmokerCreatorConfig;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingBlastEditorConfig;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingCampfireEditorConfig;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingFurnaceEditorConfig;
import pers.yufiria.craftorithm.config.menu.editor.VanillaSmeltingSmokerEditorConfig;
import pers.yufiria.craftorithm.recipe.SimpleRecipeTypes;

/**
 * 熔炼类配方菜单的类型表
 * 熔炉/高炉/烟熏炉/营火四种菜单仅配方类型与配置来源不同, 差异集中在此定义
 * 配置类的字段均为final引用, 重载配置时只刷新节点内部的值, 可安全捕获
 */
public enum SmeltingMenuType {

    FURNACE(
        SimpleRecipeTypes.VANILLA_SMELTING_FURNACE,
        new CreatorConfigs(
            VanillaSmeltingFurnaceCreatorConfig.TITLE,
            VanillaSmeltingFurnaceCreatorConfig.FRAME_ICON,
            VanillaSmeltingFurnaceCreatorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingFurnaceCreatorConfig.CONFIRM_ICON,
            VanillaSmeltingFurnaceCreatorConfig.EXP_ICON,
            VanillaSmeltingFurnaceCreatorConfig.TIME_ICON,
            VanillaSmeltingFurnaceCreatorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingFurnaceCreatorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingFurnaceCreatorConfig.CATEGORY_ICON_MISC,
            VanillaSmeltingFurnaceCreatorConfig.DEFAULT_EXP,
            VanillaSmeltingFurnaceCreatorConfig.DEFAULT_TIME
        ),
        new EditorConfigs(
            VanillaSmeltingFurnaceEditorConfig.TITLE,
            VanillaSmeltingFurnaceEditorConfig.FRAME_ICON,
            VanillaSmeltingFurnaceEditorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingFurnaceEditorConfig.CONFIRM_ICON,
            VanillaSmeltingFurnaceEditorConfig.EXP_ICON,
            VanillaSmeltingFurnaceEditorConfig.TIME_ICON,
            VanillaSmeltingFurnaceEditorConfig.BACK_ICON,
            VanillaSmeltingFurnaceEditorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingFurnaceEditorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingFurnaceEditorConfig.CATEGORY_ICON_MISC
        )
    ),
    BLAST(
        SimpleRecipeTypes.VANILLA_SMELTING_BLAST,
        new CreatorConfigs(
            VanillaSmeltingBlastCreatorConfig.TITLE,
            VanillaSmeltingBlastCreatorConfig.FRAME_ICON,
            VanillaSmeltingBlastCreatorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingBlastCreatorConfig.CONFIRM_ICON,
            VanillaSmeltingBlastCreatorConfig.EXP_ICON,
            VanillaSmeltingBlastCreatorConfig.TIME_ICON,
            VanillaSmeltingBlastCreatorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingBlastCreatorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingBlastCreatorConfig.CATEGORY_ICON_MISC,
            VanillaSmeltingBlastCreatorConfig.DEFAULT_EXP,
            VanillaSmeltingBlastCreatorConfig.DEFAULT_TIME
        ),
        new EditorConfigs(
            VanillaSmeltingBlastEditorConfig.TITLE,
            VanillaSmeltingBlastEditorConfig.FRAME_ICON,
            VanillaSmeltingBlastEditorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingBlastEditorConfig.CONFIRM_ICON,
            VanillaSmeltingBlastEditorConfig.EXP_ICON,
            VanillaSmeltingBlastEditorConfig.TIME_ICON,
            VanillaSmeltingBlastEditorConfig.BACK_ICON,
            VanillaSmeltingBlastEditorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingBlastEditorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingBlastEditorConfig.CATEGORY_ICON_MISC
        )
    ),
    SMOKER(
        SimpleRecipeTypes.VANILLA_SMELTING_SMOKER,
        new CreatorConfigs(
            VanillaSmeltingSmokerCreatorConfig.TITLE,
            VanillaSmeltingSmokerCreatorConfig.FRAME_ICON,
            VanillaSmeltingSmokerCreatorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingSmokerCreatorConfig.CONFIRM_ICON,
            VanillaSmeltingSmokerCreatorConfig.EXP_ICON,
            VanillaSmeltingSmokerCreatorConfig.TIME_ICON,
            VanillaSmeltingSmokerCreatorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingSmokerCreatorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingSmokerCreatorConfig.CATEGORY_ICON_MISC,
            VanillaSmeltingSmokerCreatorConfig.DEFAULT_EXP,
            VanillaSmeltingSmokerCreatorConfig.DEFAULT_TIME
        ),
        new EditorConfigs(
            VanillaSmeltingSmokerEditorConfig.TITLE,
            VanillaSmeltingSmokerEditorConfig.FRAME_ICON,
            VanillaSmeltingSmokerEditorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingSmokerEditorConfig.CONFIRM_ICON,
            VanillaSmeltingSmokerEditorConfig.EXP_ICON,
            VanillaSmeltingSmokerEditorConfig.TIME_ICON,
            VanillaSmeltingSmokerEditorConfig.BACK_ICON,
            VanillaSmeltingSmokerEditorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingSmokerEditorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingSmokerEditorConfig.CATEGORY_ICON_MISC
        )
    ),
    CAMPFIRE(
        SimpleRecipeTypes.VANILLA_SMELTING_CAMPFIRE,
        new CreatorConfigs(
            VanillaSmeltingCampfireCreatorConfig.TITLE,
            VanillaSmeltingCampfireCreatorConfig.FRAME_ICON,
            VanillaSmeltingCampfireCreatorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingCampfireCreatorConfig.CONFIRM_ICON,
            VanillaSmeltingCampfireCreatorConfig.EXP_ICON,
            VanillaSmeltingCampfireCreatorConfig.TIME_ICON,
            VanillaSmeltingCampfireCreatorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingCampfireCreatorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingCampfireCreatorConfig.CATEGORY_ICON_MISC,
            VanillaSmeltingCampfireCreatorConfig.DEFAULT_EXP,
            VanillaSmeltingCampfireCreatorConfig.DEFAULT_TIME
        ),
        new EditorConfigs(
            VanillaSmeltingCampfireEditorConfig.TITLE,
            VanillaSmeltingCampfireEditorConfig.FRAME_ICON,
            VanillaSmeltingCampfireEditorConfig.RESULT_FRAME_ICON,
            VanillaSmeltingCampfireEditorConfig.CONFIRM_ICON,
            VanillaSmeltingCampfireEditorConfig.EXP_ICON,
            VanillaSmeltingCampfireEditorConfig.TIME_ICON,
            VanillaSmeltingCampfireEditorConfig.BACK_ICON,
            VanillaSmeltingCampfireEditorConfig.CATEGORY_ICON_FOOD,
            VanillaSmeltingCampfireEditorConfig.CATEGORY_ICON_BLOCKS,
            VanillaSmeltingCampfireEditorConfig.CATEGORY_ICON_MISC
        )
    ),
    ;

    private final SimpleRecipeTypes recipeType;
    private final CreatorConfigs creatorConfigs;
    private final EditorConfigs editorConfigs;

    SmeltingMenuType(SimpleRecipeTypes recipeType, CreatorConfigs creatorConfigs, EditorConfigs editorConfigs) {
        this.recipeType = recipeType;
        this.creatorConfigs = creatorConfigs;
        this.editorConfigs = editorConfigs;
    }

    public SimpleRecipeTypes recipeType() {
        return recipeType;
    }

    public CreatorConfigs creatorConfigs() {
        return creatorConfigs;
    }

    public EditorConfigs editorConfigs() {
        return editorConfigs;
    }

    public record CreatorConfigs(
        StringConfig title,
        ConfigSectionConfig frameIcon,
        ConfigSectionConfig resultFrameIcon,
        ConfigSectionConfig confirmIcon,
        ConfigSectionConfig expIcon,
        ConfigSectionConfig timeIcon,
        ConfigSectionConfig categoryIconFood,
        ConfigSectionConfig categoryIconBlocks,
        ConfigSectionConfig categoryIconMisc,
        IntConfig defaultExp,
        IntConfig defaultTime
    ) {}

    public record EditorConfigs(
        StringConfig title,
        ConfigSectionConfig frameIcon,
        ConfigSectionConfig resultFrameIcon,
        ConfigSectionConfig confirmIcon,
        ConfigSectionConfig expIcon,
        ConfigSectionConfig timeIcon,
        ConfigSectionConfig backIcon,
        ConfigSectionConfig categoryIconFood,
        ConfigSectionConfig categoryIconBlocks,
        ConfigSectionConfig categoryIconMisc
    ) {}

}
