package pers.yufiria.craftorithm.config.menu;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.List;

/**
 * 内置菜单默认图标配置的构建工具，用于在 Java 中简洁地定义图标默认值
 */
public class MenuIconConfigUtils {

    /**
     * 构建一个包含 material/name/lore 的图标配置
     *
     * @param material 图标物品
     * @param name     图标名称
     * @param lore     图标描述，留空则不设置 lore
     */
    public static ConfigurationSection icon(String material, String name, String... lore) {
        ConfigurationSection config = new MemoryConfiguration();
        config.set("material", material);
        config.set("name", name);
        if (lore.length != 0) {
            config.set("lore", List.of(lore));
        }
        return config;
    }

    /**
     * 构建一个分类图标配置，lore 固定为空行 + 分类说明
     *
     * @param material           图标物品
     * @param categoryLangPrefix 分类语言的公共前缀，如 menu.recipe_editor.category
     * @param categoryNameKey    分类名称语言键，如 crafting_category_name.misc
     */
    public static ConfigurationSection categoryIcon(String material, String categoryLangPrefix, String categoryNameKey) {
        return icon(
            material,
            "<translate:lang:" + categoryLangPrefix + "_prefix> <translate:lang:" + categoryNameKey + ">",
            "",
            "<translate:lang:" + categoryLangPrefix + "_lore>"
        );
    }

    /**
     * 构建配方编辑器界面的边框图标
     */
    public static ConfigurationSection editorFrameIcon(String recipeType) {
        return icon("minecraft:gray_stained_glass_pane", "&7<translate:lang:recipe_type_name." + recipeType + ">");
    }

    /**
     * 构建配方编辑器界面的结果边框图标
     */
    public static ConfigurationSection editorResultFrameIcon() {
        return icon("minecraft:lime_stained_glass_pane", "<translate:lang:menu.recipe_editor.result_frame>");
    }

    /**
     * 构建配方编辑器界面的确认按钮图标
     */
    public static ConfigurationSection editorConfirmIcon(String material) {
        return icon(
            material,
            "<translate:lang:menu.common.confirm_edit>",
            "<translate:lang:menu.recipe_editor.confirm_lore>"
        );
    }

    /**
     * 构建配方编辑器界面的经验图标
     */
    public static ConfigurationSection editorExpIcon() {
        return icon(
            "minecraft:experience_bottle",
            "<translate:lang:menu.recipe_editor.smelting.exp>",
            "<translate:lang:menu.recipe_editor.smelting.exp_lore>"
        );
    }

    /**
     * 构建配方编辑器界面的时间图标
     */
    public static ConfigurationSection editorTimeIcon() {
        return icon(
            "minecraft:clock",
            "<translate:lang:menu.recipe_editor.smelting.time>",
            "<translate:lang:menu.recipe_editor.smelting.time_lore>"
        );
    }

    /**
     * 构建配方编辑器界面的返回按钮图标
     */
    public static ConfigurationSection editorBackIcon() {
        return icon(
            "minecraft:red_stained_glass_pane",
            "<translate:lang:menu.common.back>",
            "<translate:lang:menu.recipe_editor.back_lore>"
        );
    }

    /**
     * 构建配方编辑器界面的删除按钮图标
     */
    public static ConfigurationSection editorDeleteIcon() {
        return icon(
            "minecraft:barrier",
            "<translate:lang:menu.common.delete>",
            "<translate:lang:menu.recipe_editor.delete_lore>"
        );
    }

    /**
     * 构建配方创建器界面的边框图标
     *
     * @param recipeType    配方类型，用于拼接类型名称语言键
     * @param frameLoreKeys 边框描述的语言键后缀，如 anvil、result_right、confirm_button
     */
    public static ConfigurationSection creatorFrameIcon(String recipeType, String... frameLoreKeys) {
        String[] lore = new String[frameLoreKeys.length + 1];
        lore[0] = "";
        for (int i = 0; i < frameLoreKeys.length; i++) {
            lore[i + 1] = "<translate:lang:menu.recipe_creator.frame_lore." + frameLoreKeys[i] + ">";
        }
        return icon("minecraft:cyan_stained_glass_pane", "&b<translate:lang:recipe_type_name." + recipeType + ">", lore);
    }

    /**
     * 构建配方创建器界面的结果边框图标
     */
    public static ConfigurationSection creatorResultFrameIcon(String resultLoreKey) {
        return icon(
            "minecraft:lime_stained_glass_pane",
            "<translate:lang:menu.recipe_creator.result_frame>",
            "",
            "<translate:lang:menu.recipe_creator.result_lore." + resultLoreKey + ">"
        );
    }

    /**
     * 构建配方创建器界面的确认按钮图标
     */
    public static ConfigurationSection creatorConfirmIcon(String material, String confirmLoreKey) {
        return icon(
            material,
            "<translate:lang:menu.common.confirm_create>",
            "",
            "<translate:lang:menu.recipe_creator.confirm_lore." + confirmLoreKey + ">"
        );
    }

    /**
     * 构建配方创建器界面的经验图标
     */
    public static ConfigurationSection creatorExpIcon() {
        return icon(
            "minecraft:experience_bottle",
            "<translate:lang:menu.recipe_creator.smelting.exp>",
            "",
            "<translate:lang:menu.recipe_creator.smelting.exp_lore>"
        );
    }

    /**
     * 构建配方创建器界面的时间图标
     */
    public static ConfigurationSection creatorTimeIcon() {
        return icon(
            "minecraft:clock",
            "<translate:lang:menu.recipe_creator.smelting.time>",
            "",
            "<translate:lang:menu.recipe_creator.smelting.time_lore>"
        );
    }

    /**
     * 向配方展示界面的图标配置写入返回按钮
     */
    public static void setDisplayBackButton(ConfigurationSection icons, String key) {
        icons.set(key + ".material", "minecraft:red_stained_glass_pane");
        icons.set(key + ".name", "<translate:lang:menu.recipe_display.back>");
        icons.set(key + ".actions.left", List.of("back()"));
    }

}
