package pers.yufiria.craftorithm.util;

import crypticlib.MinecraftVersion;
import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

import java.util.List;
import java.util.Set;

public class RecipeUtils {

    private static final Set<ClickType> quickCraftClickTypes;

    static {
        //虽然原版1.21.2就可以用CTRL+Q进行快速合成，但是paper端1.21.11才有这个功能
        if (MinecraftVersion.current().after(MinecraftVersion.V1_21_10)) {
            quickCraftClickTypes = Set.of(
                ClickType.SHIFT_LEFT,
                ClickType.SHIFT_RIGHT,
                ClickType.CONTROL_DROP
            );
        } else {
            quickCraftClickTypes = Set.of(
                ClickType.SHIFT_LEFT,
                ClickType.SHIFT_RIGHT
            );
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

    /**
     * 计算本次合成的次数
     * 会考虑玩家背包容量
     * @param event 合成事件
     * @return 本次合成进行的次数
     */
    public static int calculateVanillaCraftNum(CraftItemEvent event) {
        CraftingInventory inventory = event.getInventory();
        return calculateVanillaCraftNum(
            event.getClick(),
            inventory.getMatrix(),
            inventory.getResult(),
            event.getWhoClicked()
        );
    }

    /**
     * 计算本次锻造的次数
     * 会考虑玩家背包容量
     * @param event 锻造事件
     * @return 本次锻造进行的次数
     */
    public static int calculateVanillaCraftNum(SmithItemEvent event) {
        SmithingInventory inventory = event.getInventory();
        return calculateVanillaCraftNum(
            event.getClick(),
            new ItemStack[] {
                inventory.getItem(0),
                inventory.getItem(1),
                inventory.getItem(2)
            },
            inventory.getResult(),
            event.getWhoClicked()
        );
    }

    /**
     * 计算在指定点击方式下,实际合成的次数
     * 会计算玩家背包容量
     * @param click 点击方式
     * @param matrix 本次合成所用的所有物品
     * @param result 结果物品
     * @param player 进行合成的玩家
     * @return 实际合成的次数
     */
    public static int calculateVanillaCraftNum(ClickType click, ItemStack[] matrix, ItemStack result, HumanEntity player) {
        // 普通点击只合成1个
        if (!quickCraftClickTypes.contains(click)) {
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

    private static int calculateCanFit(HumanEntity player, ItemStack result, int maxNeeded) {
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

}
