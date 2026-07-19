package pers.yufiria.craftorithm.util;

import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.api.recipe.choice.CustomRecipeChoice;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RecipeUtils {

    private static final Map<NamespacedKey, Tag<Material>> ITEMS_TAGS = new HashMap<>();
    private static final Map<NamespacedKey, Tag<Material>> BLOCKS_TAGS = new HashMap<>();

    static {
        Iterable<Tag<Material>> blocksTags = Bukkit.getTags("blocks", Material.class);
        for (Tag<Material> blocksTag : blocksTags) {
            BLOCKS_TAGS.put(blocksTag.getKey(), blocksTag);
        }

        Iterable<Tag<Material>> itemsTags = Bukkit.getTags("items", Material.class);
        for (Tag<Material> itemsTag : itemsTags) {
            ITEMS_TAGS.put(itemsTag.getKey(), itemsTag);
        }
    }

    /**
     * 移除配方形状中首尾的全空白行（保留中间的空行）
     */
    public static void removeEmptyRow(List<String> shape) {
        while (!shape.isEmpty() && shape.getFirst().trim().isEmpty()) {
            shape.removeFirst();
        }
        while (!shape.isEmpty() && shape.getLast().trim().isEmpty()) {
            shape.removeLast();
        }
    }

    /**
     * 移除配方形状中首尾的全空白列（保留中间的空列）
     */
    public static void removeEmptyColumn(List<String> shape) {
        boolean[] empty = new boolean[3];
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            empty[i] = shape.stream().allMatch(s -> finalI >= s.length() || s.charAt(finalI) == ' ');
        }
        if (empty[0]) {
            if (empty[1]) {
                if (!empty[2]) {
                    shape.replaceAll(s -> s.length() > 2 ? s.substring(2) : "");
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.length() >= 2 ? s.substring(1, 2) : "");
                } else {
                    shape.replaceAll(s -> s.length() >= 2 ? s.substring(1) : "");
                }
            }
        } else {
            if (empty[1]) {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, 1));
                }
            } else {
                if (empty[2]) {
                    shape.replaceAll(s -> s.substring(0, Math.min(2, s.length())));
                }
            }
        }
    }

    public static Optional<Tag<Material>> getTag(String tagKeyStr) {
        if (tagKeyStr.contains(":")) {
            NamespacedKey tagKey = NamespacedKey.fromString(tagKeyStr);
            return Optional.ofNullable(getTag(tagKey));
        }
        //兼容旧版写法, 也就是直接在Tag里反射
        String upperTagKey = tagKeyStr.toUpperCase();
        Tag<Material> reflectTag;
        try {
            Field field = Tag.class.getField(upperTagKey);
            reflectTag = (Tag<Material>) field.get(null);
            return Optional.ofNullable(reflectTag);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        NamespacedKey tagKey = NamespacedKey.fromString(tagKeyStr);
        return Optional.ofNullable(getTag(tagKey));
    }

    private static @Nullable Tag<Material> getTag(NamespacedKey tagKey) {
        if (ITEMS_TAGS.containsKey(tagKey)) {
            return ITEMS_TAGS.get(tagKey);
        }
        if (BLOCKS_TAGS.containsKey(tagKey)) {
            return BLOCKS_TAGS.get(tagKey);
        }
        return null;
    }

    /**
     * 回溯匹配
     * 用于无序配方判断材料是否足够
     * @param items
     * @param choices
     * @return
     */
    @ApiStatus.Internal
    public static boolean matchItemsToChoices(
        List<ItemStack> items,
        List<RecipeChoice> choices
    ) {
        // 使用一个 boolean 数组标记成分是否已被使用
        boolean[] used = new boolean[choices.size()];
        return backtrack(items, choices, used, 0);
    }

    private static boolean backtrack(
        List<ItemStack> items,
        List<RecipeChoice> choices,
        boolean[] used,
        int itemIndex
    ) {
        // 所有物品都已匹配成功
        if (itemIndex == items.size()) {
            return true;
        }
        org.bukkit.inventory.ItemStack currentItem = items.get(itemIndex);
        // 尝试将当前物品匹配到任意一个未使用的成分
        for (int i = 0; i < choices.size(); i++) {
            if (!used[i] && choices.get(i).test(currentItem)) {
                used[i] = true;
                if (backtrack(items, choices, used, itemIndex + 1)) {
                    return true;
                }
                used[i] = false; // 回溯
            }
        }
        return false;
    }

    public static int calculateVanillaCraftNum(ClickType click, ItemStack[] matrix, ItemStack result, Player player) {
        // 普通点击只合成1个
        if (click != ClickType.SHIFT_LEFT
            && click != ClickType.SHIFT_RIGHT
            && click != ClickType.CONTROL_DROP) {
            return 1;
        }
        if (matrix == null) return 0;
        int minIngredientAmount = Integer.MAX_VALUE;
        for (ItemStack item : matrix) {
            if (item == null || item.isEmpty()) continue;
            minIngredientAmount = Math.min(minIngredientAmount, item.getAmount());
        }
        if (minIngredientAmount == Integer.MAX_VALUE) return 1;
        // Ctrl+丢弃：合成最大数量，不受背包空间限制
        if (click == ClickType.CONTROL_DROP) {
            return minIngredientAmount;
        }
        if (ItemHelper.isAir(result)) return 1;
        int resultAmount = result.getAmount();
        // 计算背包能装下多少个结果物品（向上取整，适配原版行为）
        int maxNeeded = minIngredientAmount * resultAmount;
        int canFit = calculateCanFit(player, result, maxNeeded);
        int canFitTimes = (canFit + resultAmount - 1) / resultAmount;
        return Math.max(1, Math.min(minIngredientAmount, canFitTimes));
    }

    private static int calculateCanFit(Player player, ItemStack result, int maxNeeded) {
        if (ItemHelper.isAir(result)) return 0;
        int maxStack = result.getType().getMaxStackSize();
        int space = 0;
        Material resultType = result.getType();
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.isEmpty()) {
                space += maxStack;
            } else if (item.getType() == resultType && item.isSimilar(result)) {
                space += maxStack - item.getAmount();
            }
            // 提前退出：空间已经足够
            if (space >= maxNeeded) {
                return space;
            }
        }
        return space;
    }

    public static RecipeChoice getBukkitChoice(RecipeChoice recipeChoice) {
        if (recipeChoice instanceof CustomRecipeChoice customRecipeChoice) {
            RecipeChoice bukkitChoice = customRecipeChoice.bukkitChoice();
            return RecipeUtils.getBukkitChoice(bukkitChoice);
        }
        return recipeChoice;
    }

    public static boolean testOptionalChoice(Optional<RecipeChoice> choice, ItemStack inputItem) {
        Optional<Boolean> result = choice.map(ingredient -> ingredient.test(inputItem));
        return result.orElseGet(() -> ItemHelper.isAir(inputItem));
    }
}
