package pers.yufiria.craftorithm.recipe.finger;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 配方指纹生成工具。<p>
 * 从配方 YAML 配置中解析 shape / ingredients，生成规范指纹字符串，
 * 并返回该配方使用的 tag / itempack 反查映射。
 */
public class RecipeFingerGenerator {

    /**
     * 根据配方配置生成规范指纹和反查映射。
     */
    public static GenerationResult generate(YamlConfiguration recipeConfig) {
        Set<CanonicalMapping> canonicalMappings = new LinkedHashSet<>();
        List<String> shape = recipeConfig.getStringList("shape");
        List<String> fingers;

        if (!shape.isEmpty()) {
            ConfigurationSection ingredientsConfig = recipeConfig.getConfigurationSection("ingredients");
            if (ingredientsConfig == null) {
                return GenerationResult.EMPTY;
            }
            fingers = generateShapedFingers(shape, ingredientsConfig, canonicalMappings);
        } else {
            List<String> ingredientList = recipeConfig.getStringList("ingredients");
            if (ingredientList.isEmpty()) {
                return GenerationResult.EMPTY;
            }
            fingers = generateShapelessFingers(ingredientList, canonicalMappings);
        }

        if (fingers.isEmpty()) {
            return GenerationResult.EMPTY;
        }
        return new GenerationResult(List.copyOf(fingers), Set.copyOf(canonicalMappings));
    }

    // ====================== 有序配方 ======================

    private static List<String> generateShapedFingers(
        List<String> shape,
        ConfigurationSection ingredientsConfig,
        Set<CanonicalMapping> canonicalMappings
    ) {
        List<String> trimmedShape = new ArrayList<>(shape);
        RecipeUtils.removeEmptyRow(trimmedShape);
        RecipeUtils.removeEmptyColumn(trimmedShape);

        if (trimmedShape.isEmpty()) {
            return List.of();
        }

        int rows = trimmedShape.size();
        int cols = trimmedShape.stream().mapToInt(String::length).max().orElse(0);
        String[][] canonicalGrid = new String[rows][cols];
        for (int row = 0; row < rows; row++) {
            String rowStr = trimmedShape.get(row);
            for (int col = 0; col < cols; col++) {
                if (col >= rowStr.length()) {
                    continue;
                }
                char c = rowStr.charAt(col);
                if (c == ' ') {
                    continue;
                }
                String choiceStr = ingredientsConfig.getString(String.valueOf(c));
                if (choiceStr == null) {
                    continue;
                }
                canonicalGrid[row][col] = toCanonicalForm(choiceStr, canonicalMappings);
            }
        }

        String original = buildShapedFinger(canonicalGrid, rows, cols, false);
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

    private static List<String> generateShapelessFingers(
        List<String> ingredientList,
        Set<CanonicalMapping> canonicalMappings
    ) {
        List<String> canonicals = new ArrayList<>();
        for (String choiceStr : ingredientList) {
            if (choiceStr == null || choiceStr.isEmpty()) {
                continue;
            }
            canonicals.add(toCanonicalForm(choiceStr, canonicalMappings));
        }

        if (canonicals.isEmpty()) {
            return List.of();
        }

        canonicals.sort(null);
        return List.of(String.join("|", canonicals));
    }

    // ====================== 规范化 ======================

    private static String toCanonicalForm(String choiceStr, Set<CanonicalMapping> canonicalMappings) {
        if (choiceStr == null || choiceStr.isEmpty()) {
            return choiceStr;
        }

        if (!choiceStr.contains(":")) {
            Material material = Material.matchMaterial(choiceStr);
            if (material != null) {
                return NamespacedItemId.fromMaterial(material).toString();
            }
            return "minecraft:" + choiceStr.toLowerCase(Locale.ROOT);
        }

        int index = choiceStr.indexOf(":");
        String namespace = choiceStr.substring(0, index).toLowerCase(Locale.ROOT);
        String body = choiceStr.substring(index + 1);

        return switch (namespace) {
            case "minecraft" -> {
                Material material = Material.matchMaterial(choiceStr);
                if (material != null) {
                    yield NamespacedItemId.fromMaterial(material).toString();
                }
                yield choiceStr.toLowerCase(Locale.ROOT);
            }
            case "tag" -> {
                registerTagMappings(choiceStr, canonicalMappings);
                yield choiceStr;
            }
            case "item_pack" -> {
                registerItemPackMappings(choiceStr, body, canonicalMappings);
                yield choiceStr;
            }
            default -> choiceStr;
        };
    }

    private static void registerTagMappings(String tagChoiceStr, Set<CanonicalMapping> canonicalMappings) {
        String tagKeyStr = tagChoiceStr.substring("tag:".length());
        Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
        if (tagOpt.isEmpty()) {
            return;
        }

        for (Material material : tagOpt.get().getValues()) {
            String concreteId = NamespacedItemId.fromMaterial(material).toString();
            canonicalMappings.add(new CanonicalMapping(concreteId, tagChoiceStr));
        }
    }

    private static void registerItemPackMappings(
        String packChoiceStr,
        String packId,
        Set<CanonicalMapping> canonicalMappings
    ) {
        ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packId);
        if (itemPack == null) {
            return;
        }

        for (NamespacedItemIdStack stackedId : itemPack.itemIds()) {
            canonicalMappings.add(new CanonicalMapping(stackedId.itemId().toString(), packChoiceStr));
        }
    }

    public record CanonicalMapping(String concreteId, String canonicalForm) {}

    public record GenerationResult(List<String> fingers, Set<CanonicalMapping> canonicalMappings) {

        private static final GenerationResult EMPTY = new GenerationResult(List.of(), Set.of());

    }

}
