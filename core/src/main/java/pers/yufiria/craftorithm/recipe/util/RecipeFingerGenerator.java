package pers.yufiria.craftorithm.recipe.util;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeFingerManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.*;

/**
 * 配方指纹生成工具。<p>
 * 从配方 YAML 配置中解析 shape / ingredients，生成规范指纹字符串。
 * 同时处理 tag / itempack 的反查映射注册。
 */
public class RecipeFingerGenerator {

    /**
     * 根据配方配置生成所有规范指纹。
     * <ul>
     *   <li>有序配方 (有 shape 字段): 生成原始 + 水平翻转两条指纹</li>
     *   <li>无序配方 (无 shape 字段): 生成一条排序后的指纹</li>
     * </ul>
     *
     * @param recipeConfig 配方 YAML 配置
     * @return 规范指纹列表，无法解析时返回空列表
     */
    public static List<String> generateFingers(YamlConfiguration recipeConfig) {
        List<String> shape = recipeConfig.getStringList("shape");

        if (!shape.isEmpty()) {
            // 有序配方：ingredients 是 ConfigurationSection (key=字符, value=choice字符串)
            ConfigurationSection ingredientsConfig = recipeConfig.getConfigurationSection("ingredients");
            if (ingredientsConfig == null) {
                return List.of();
            }
            return generateShapedFingers(shape, ingredientsConfig);
        } else {
            // 无序配方：ingredients 是 List<String>
            List<String> ingredientList = recipeConfig.getStringList("ingredients");
            if (ingredientList.isEmpty()) {
                return List.of();
            }
            return generateShapelessFingers(ingredientList);
        }
    }

    // ====================== 有序配方 ======================

    /**
     * 生成有序配方的规范指纹（原始 + 水平翻转）。
     */
    private static List<String> generateShapedFingers(List<String> shape, ConfigurationSection ingredientsConfig) {
        // 去除首尾空白行（复用 RecipeUtils 的逻辑）
        List<String> trimmedShape = new ArrayList<>(shape);
        RecipeUtils.removeEmptyRow(trimmedShape);
        RecipeUtils.removeEmptyColumn(trimmedShape);

        if (trimmedShape.isEmpty()) {
            return List.of();
        }

        int rows = trimmedShape.size();
        int cols = trimmedShape.stream().mapToInt(String::length).max().orElse(0);

        // 解析每个格子的规范形式
        String[][] canonicalGrid = new String[rows][cols];
        for (int row = 0; row < rows; row++) {
            String rowStr = trimmedShape.get(row);
            for (int col = 0; col < cols; col++) {
                if (col >= rowStr.length()) {
                    canonicalGrid[row][col] = null;
                    continue;
                }
                char c = rowStr.charAt(col);
                if (c == ' ') {
                    canonicalGrid[row][col] = null;
                    continue;
                }
                String choiceStr = ingredientsConfig.getString(String.valueOf(c));
                if (choiceStr == null) {
                    canonicalGrid[row][col] = null;
                    continue;
                }
                canonicalGrid[row][col] = toCanonicalForm(choiceStr);
            }
        }

        // 生成原始指纹
        String original = buildShapedFinger(canonicalGrid, rows, cols, false);
        // 生成水平翻转指纹
        String flipped = buildShapedFinger(canonicalGrid, rows, cols, true);

        List<String> fingers = new ArrayList<>(2);
        if (original != null) {
            fingers.add(original);
        }
        if (flipped != null && !flipped.equals(original)) {
            fingers.add(flipped);
        }
        return fingers;
    }

    /**
     * 构建有序配方的指纹字符串。
     *
     * @param grid   规范形式网格
     * @param rows   行数
     * @param cols   列数
     * @param flip   是否水平翻转
     */
    private static @Nullable String buildShapedFinger(String[][] grid, int rows, int cols, boolean flip) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int actualCol = flip ? (cols - 1 - col) : col;
                String canonical = grid[row][actualCol];
                if (canonical == null) {
                    continue;
                }
                if (!first) {
                    sb.append('|');
                }
                first = false;
                sb.append('<').append(row).append(',').append(col).append(',').append(canonical).append('>');
            }
        }
        return first ? null : sb.toString();
    }

    // ====================== 无序配方 ======================

    /**
     * 生成无序配方的规范指纹（排序，无坐标）。
     */
    private static List<String> generateShapelessFingers(List<String> ingredientList) {
        List<String> canonicals = new ArrayList<>();
        for (String choiceStr : ingredientList) {
            if (choiceStr == null || choiceStr.isEmpty()) {
                continue;
            }
            canonicals.add(toCanonicalForm(choiceStr));
        }

        if (canonicals.isEmpty()) {
            return List.of();
        }

        // 按字典序排序
        canonicals.sort(null);
        String finger = String.join("|", canonicals);
        return List.of(finger);
    }

    // ====================== 规范化 ======================

    /**
     * 将配方配置中的 choice 字符串转为规范形式。<p>
     * 同时为 tag / itempack 注册反查映射。
     *
     * @param choiceStr 配方配置中的材料字符串
     * @return 规范形式字符串
     */
    public static String toCanonicalForm(String choiceStr) {
        if (choiceStr == null || choiceStr.isEmpty()) {
            return choiceStr;
        }

        if (!choiceStr.contains(":")) {
            // 无命名空间，标准化为 minecraft:
            Material material = Material.matchMaterial(choiceStr);
            if (material != null) {
                return NamespacedItemId.fromMaterial(material).toString();
            }
            return "minecraft:" + choiceStr.toLowerCase(Locale.ROOT);
        }

        int index = choiceStr.indexOf(":");
        String namespace = choiceStr.substring(0, index).toLowerCase(Locale.ROOT);
        String body = choiceStr.substring(index + 1);

        switch (namespace) {
            case "minecraft":
                // 标准化 material 名称
                Material material = Material.matchMaterial(choiceStr);
                if (material != null) {
                    return NamespacedItemId.fromMaterial(material).toString();
                }
                return choiceStr.toLowerCase(Locale.ROOT);

            case "tag":
                // tag:minecraft:planks → 注册反查映射
                registerTagMappings(choiceStr);
                return choiceStr;

            case "item_pack":
                // item_pack:my_pack → 注册反查映射
                registerItemPackMappings(choiceStr, body);
                return choiceStr;

            default:
                // 自定义物品 (itemsadder:ruby 等)，保持原样
                return choiceStr.toLowerCase(Locale.ROOT);
        }
    }

    /**
     * 为 tag 注册所有 Material 的反查映射。
     */
    private static void registerTagMappings(String tagChoiceStr) {
        String tagKeyStr = tagChoiceStr.substring("tag:".length());
        Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
        if (tagOpt.isEmpty()) {
            return;
        }
        Tag<Material> tag = tagOpt.get();
        for (Material m : tag.getValues()) {
            String concreteId = NamespacedItemId.fromMaterial(m).toString();
            RecipeFingerManager.INSTANCE.registerCanonicalMapping(concreteId, tagChoiceStr);
        }
    }

    /**
     * 为 itempack 注册所有物品的反查映射。
     */
    private static void registerItemPackMappings(String packChoiceStr, String packId) {
        ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packId);
        if (itemPack == null) {
            return;
        }
        for (NamespacedItemIdStack stackedId : itemPack.itemIds()) {
            String concreteId = stackedId.itemId().toString();
            RecipeFingerManager.INSTANCE.registerCanonicalMapping(concreteId, packChoiceStr);
        }
    }

}
