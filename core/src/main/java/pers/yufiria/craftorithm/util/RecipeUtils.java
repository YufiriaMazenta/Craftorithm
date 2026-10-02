package pers.yufiria.craftorithm.util;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.Key;
import crypticlib.MinecraftVersion;
import crypticlib.util.ItemHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;
import pers.yufiria.craftorithm.recipe.RecipeManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
     * 移除配方形状中首尾的全空白行与全空白列（保留中间的空行/空列）
     */
    public static void trimShape(List<String> shape) {
        while (!shape.isEmpty() && shape.getFirst().trim().isEmpty()) {
            shape.removeFirst();
        }
        while (!shape.isEmpty() && shape.getLast().trim().isEmpty()) {
            shape.removeLast();
        }
        if (shape.isEmpty()) {
            return;
        }
        //找出最左侧与最右侧的非空白字符所在的列
        int firstColumn = Integer.MAX_VALUE;
        int lastColumn = -1;
        for (String row : shape) {
            for (int i = 0; i < row.length(); i++) {
                if (row.charAt(i) != ' ') {
                    firstColumn = Math.min(firstColumn, i);
                    lastColumn = Math.max(lastColumn, i);
                }
            }
        }
        //整行都是空白,无需处理
        if (lastColumn < 0) {
            return;
        }
        int start = firstColumn;
        int end = lastColumn + 1;
        shape.replaceAll(row -> row.length() > start ? row.substring(start, Math.min(end, row.length())) : "");
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

    /**
     * 为在线的所有玩家解锁配方（支持正则表达式）
     * @param recipeKeyPattern 配方key的正则表达式,如果不是正则则解析为{@link NamespacedKey}
     */
    public static void discoverRecipeForOnlinePlayers(String recipeKeyPattern) {
        CrypticLibBukkit.scheduler().async(() -> {
            List<NamespacedKey> discoverRecipes = matchRecipeKeys(recipeKeyPattern);
            for (Player player : Bukkit.getOnlinePlayers()) {
                CrypticLibBukkit.scheduler().runOnEntity(player, () -> {
                    player.discoverRecipes(discoverRecipes);
                });
            }
        });
    }

    /**
     * 为玩家解锁配方(支持正则表达式)
     * @param target 要解锁的玩家
     * @param recipeKeyPattern 配方key的正则表达式,如果不是正则则解析为{@link NamespacedKey}
     * @param callback 解锁完毕后的回调
    **/
    public static void discoverRecipe(Player target, String recipeKeyPattern, Consumer<Integer> callback) {
        CrypticLibBukkit.scheduler().async(() -> {
            List<NamespacedKey> discoverRecipes = matchRecipeKeys(recipeKeyPattern);
            CrypticLibBukkit.scheduler().runOnEntity(target, () -> {
                callback.accept(target.discoverRecipes(discoverRecipes));
            }, () -> {});
        });
    }

    /**
     * 为玩家取消解锁配方(支持正则表达式)
     * @param target 要解锁的玩家
     * @param recipeKeyPattern 配方key的正则表达式,如果不是正则则解析为{@link NamespacedKey}
     * @param callback 取消解锁完毕后的回调
     **/
    public static void undiscoverRecipe(Player target, String recipeKeyPattern, Consumer<Integer> callback) {
        CrypticLibBukkit.scheduler().async(() -> {
            List<NamespacedKey> undiscoverRecipes = matchRecipeKeys(recipeKeyPattern);
            CrypticLibBukkit.scheduler().runOnEntity(target, () -> {
                callback.accept(target.undiscoverRecipes(undiscoverRecipes));
            }, () -> {});
        });
    }

    /**
     * 为在线的所有玩家取消解锁配方（支持正则表达式）
     * @param recipeKeyPattern 配方key的正则表达式,如果不是正则则解析为{@link NamespacedKey}
     */
    public static void undiscoverRecipeForOnlinePlayers(String recipeKeyPattern) {
        CrypticLibBukkit.scheduler().async(() -> {
            List<NamespacedKey> undiscoverRecipes = matchRecipeKeys(recipeKeyPattern);
            for (Player player : Bukkit.getOnlinePlayers()) {
                CrypticLibBukkit.scheduler().runOnEntity(player, () -> {
                    player.undiscoverRecipes(undiscoverRecipes);
                });
            }
        });
    }

    public static List<NamespacedKey> matchRecipeKeys(String recipeKeyPattern) {
        Pattern pattern;
        try {
            pattern = Pattern.compile(recipeKeyPattern);
        } catch (PatternSyntaxException e) {
            pattern = null;
        }

        final Pattern finalPattern = pattern;
        List<NamespacedKey> recipeKeys;
        if (pattern == null) {
            Key key = Key.key(recipeKeyPattern);
            if (key == null) {
                CrypticLib.info("&cInvalid recipe key <key>", Map.of("<key>", recipeKeyPattern));
                return Collections.emptyList();
            }
            recipeKeys = List.of(new NamespacedKey(key.namespace(), key.key()));
        } else {
            recipeKeys = RecipeManager.INSTANCE.serverRecipeKeys().stream()
                .filter(key -> finalPattern.matcher(key.toString()).matches()).toList();
        }
        return recipeKeys;
    }


}
