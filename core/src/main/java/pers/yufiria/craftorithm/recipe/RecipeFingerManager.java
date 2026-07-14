package pers.yufiria.craftorithm.recipe;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.util.RecipeFingerGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配方指纹管理器。<p>
 * 通过规范指纹（canonical finger）实现基于 Craftorithm 物品 ID 的配方快速查找，
 * 替代 Bukkit 原生匹配，支持 tag / itempack 等多物品材料。
 */
@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = 3),
        @TaskRule(lifeCycle = LifeCycle.RELOAD, priority = 3)
    }
)
public enum RecipeFingerManager implements LifeCycleTask {

    INSTANCE;

    /** 规范指纹 → 配方 Key（主查询表） */
    private final Map<String, NamespacedKey> fingerToRecipeKey = new ConcurrentHashMap<>();

    /** 配方 Key → 它注册的所有规范指纹（卸载时清理用） */
    private final Map<NamespacedKey, List<String>> recipeKeyToFingers = new ConcurrentHashMap<>();

    /**
     * 具体物品 ID → 它所属的规范形式（反查表）。<p>
     * 一个物品可能同时属于多个 tag，所以 value 是 Set。
     */
    private final Map<String, Set<String>> concreteToCanonical = new ConcurrentHashMap<>();

    // ====================== 注册 / 注销 ======================

    /**
     * 根据配方配置构建并注册指纹。
     *
     * @param recipeKey  配方 NamespacedKey
     * @param recipeConfig 配方 YAML 配置
     */
    public void registerRecipeFinger(NamespacedKey recipeKey, YamlConfiguration recipeConfig) {
        List<String> fingers = RecipeFingerGenerator.generateFingers(recipeConfig);
        if (fingers.isEmpty()) {
            return;
        }
        for (String finger : fingers) {
            fingerToRecipeKey.put(finger, recipeKey);
        }
        recipeKeyToFingers.put(recipeKey, fingers);
    }

    /**
     * 注销一个配方的所有指纹。
     */
    public void unregisterRecipeFinger(NamespacedKey recipeKey) {
        List<String> fingers = recipeKeyToFingers.remove(recipeKey);
        if (fingers != null) {
            fingers.forEach(fingerToRecipeKey::remove);
        }
    }

    // ====================== 反查表 ======================

    /**
     * 注册一个具体物品到规范形式的反查映射。<p>
     * 在解析 tag / itempack 配方材料时调用。
     *
     * @param concreteId    具体物品 ID，如 "minecraft:oak_planks"
     * @param canonicalForm 规范形式，如 "tag:minecraft:planks"
     */
    public void registerCanonicalMapping(String concreteId, String canonicalForm) {
        concreteToCanonical.computeIfAbsent(concreteId, k -> ConcurrentHashMap.newKeySet())
            .add(canonicalForm);
    }

    /**
     * 获取一个具体物品 ID 对应的所有规范形式。<p>
     * 如果没有注册过反查映射，返回空集合。
     */
    public Set<String> getCanonicalForms(String concreteId) {
        return concreteToCanonical.getOrDefault(concreteId, Collections.emptySet());
    }

    // ====================== 查询 ======================

    /**
     * 根据合成网格物品查找配方 Key。
     *
     * @param matrix 合成网格（3x3，可能包含 null）
     * @return 匹配到的配方 Key，未匹配返回 null
     */
    public @Nullable NamespacedKey findRecipeByGrid(ItemStack[] matrix) {
        // 转为二维列表
        List<List<String>> grid = new ArrayList<>();
        int cols = (int) Math.sqrt(matrix.length);
        for (int row = 0; row < cols; row++) {
            List<String> rowList = new ArrayList<>();
            for (int col = 0; col < cols; col++) {
                ItemStack item = matrix[row * cols + col];
                if (item == null || item.getType().isAir()) {
                    rowList.add(null);
                } else {
                    NamespacedItemIdStack idStack = ItemManager.INSTANCE.matchItemId(item, true);
                    String concreteId;
                    if (idStack != null) {
                        concreteId = idStack.itemId().toString();
                    } else {
                        concreteId = NamespacedItemId.fromMaterial(item.getType()).toString();
                    }
                    rowList.add(concreteId);
                }
            }
            grid.add(rowList);
        }

        // 裁剪空行空列
        pers.yufiria.craftorithm.util.CollectionsUtils.trimEmptyBorders(grid, Objects::isNull);

        if (grid.isEmpty()) {
            return null;
        }

        // 尝试有序配方指纹（带坐标）
        String finger = buildCanonicalFinger(grid);
        NamespacedKey key = fingerToRecipeKey.get(finger);
        if (key != null) {
            return key;
        }

        // 尝试无序配方指纹（排序，无坐标）
        String shapelessFinger = buildShapelessFinger(grid);
        key = fingerToRecipeKey.get(shapelessFinger);
        if (key != null) {
            return key;
        }

        // fallback: 尝试每个格子的所有规范形式组合
        return findWithCanonicalExpansion(grid);
    }

    /**
     * 用裁剪后的 grid 生成无序配方规范指纹（排序，无坐标）。
     */
    private String buildShapelessFinger(List<List<String>> grid) {
        List<String> canonicals = new ArrayList<>();
        for (List<String> row : grid) {
            for (String concreteId : row) {
                if (concreteId == null) {
                    continue;
                }
                canonicals.add(toCanonicalForm(concreteId));
            }
        }
        if (canonicals.isEmpty()) {
            return "";
        }
        canonicals.sort(null);
        return String.join("|", canonicals);
    }

    /**
     * 用裁剪后的 grid 生成规范指纹（有序配方格式：带坐标）。
     * 每个格子取第一个可用的规范形式。
     */
    private String buildCanonicalFinger(List<List<String>> grid) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int row = 0; row < grid.size(); row++) {
            List<String> rowList = grid.get(row);
            for (int col = 0; col < rowList.size(); col++) {
                String concreteId = rowList.get(col);
                if (concreteId == null) {
                    continue;
                }
                if (!first) {
                    sb.append('|');
                }
                first = false;
                sb.append('<').append(row).append(',').append(col).append(',');
                sb.append(toCanonicalForm(concreteId));
                sb.append('>');
            }
        }
        return sb.toString();
    }

    /**
     * 将具体物品 ID 转为规范形式。<p>
     * 优先使用反查表中第一个匹配的规范形式，否则返回原 ID。
     */
    private String toCanonicalForm(String concreteId) {
        Set<String> canonicals = concreteToCanonical.get(concreteId);
        if (canonicals != null && !canonicals.isEmpty()) {
            return canonicals.iterator().next();
        }
        return concreteId;
    }

    /**
     * 当直接匹配失败时，尝试每个格子的所有规范形式组合。<p>
     * 用于处理一个物品同时属于多个 tag 的边界情况。
     */
    private @Nullable NamespacedKey findWithCanonicalExpansion(List<List<String>> grid) {
        // 收集每个格子的所有可能规范形式
        List<List<String>> perSlotCanonicals = new ArrayList<>();
        for (List<String> row : grid) {
            for (String concreteId : row) {
                if (concreteId == null) {
                    perSlotCanonicals.add(List.of());
                } else {
                    Set<String> canonicals = concreteToCanonical.get(concreteId);
                    if (canonicals != null && !canonicals.isEmpty()) {
                        perSlotCanonicals.add(new ArrayList<>(canonicals));
                    } else {
                        perSlotCanonicals.add(List.of(concreteId));
                    }
                }
            }
        }

        // 收集所有非空格子的索引
        List<Integer> nonEmptyIndices = new ArrayList<>();
        for (int i = 0; i < perSlotCanonicals.size(); i++) {
            if (!perSlotCanonicals.get(i).isEmpty()) {
                nonEmptyIndices.add(i);
            }
        }

        if (nonEmptyIndices.isEmpty()) {
            return null;
        }

        // 找到有多个规范形式的格子（需要尝试组合的）
        List<Integer> multiIndices = nonEmptyIndices.stream()
            .filter(i -> perSlotCanonicals.get(i).size() > 1)
            .toList();

        // 如果没有多选格子，说明之前已经匹配过了，直接返回 null
        if (multiIndices.isEmpty()) {
            return null;
        }

        // 回溯尝试所有多选格子的组合
        int[] choices = new int[multiIndices.size()];
        int cols = grid.isEmpty() ? 0 : grid.getFirst().size();

        while (true) {
            // 收集当前组合的规范形式，同时生成有序和无序指纹
            List<String> currentCanonicals = new ArrayList<>();
            StringBuilder shapedSb = new StringBuilder();
            boolean first = true;
            int multiIdx = 0;
            for (int slotIdx : nonEmptyIndices) {
                if (!first) {
                    shapedSb.append('|');
                }
                first = false;
                int row = slotIdx / cols;
                int col = slotIdx % cols;
                shapedSb.append('<').append(row).append(',').append(col).append(',');

                boolean isMulti = multiIndices.contains(slotIdx);
                String canonical;
                if (isMulti) {
                    canonical = perSlotCanonicals.get(slotIdx).get(choices[multiIdx]);
                } else {
                    canonical = perSlotCanonicals.get(slotIdx).getFirst();
                }
                shapedSb.append(canonical).append('>');
                currentCanonicals.add(canonical);
                if (isMulti) {
                    multiIdx++;
                }
            }

            // 尝试有序指纹
            NamespacedKey key = fingerToRecipeKey.get(shapedSb.toString());
            if (key != null) {
                return key;
            }

            // 尝试无序指纹
            List<String> sorted = new ArrayList<>(currentCanonicals);
            sorted.sort(null);
            String shapelessFinger = String.join("|", sorted);
            key = fingerToRecipeKey.get(shapelessFinger);
            if (key != null) {
                return key;
            }

            // 递增选择
            boolean overflow = true;
            for (int i = 0; i < choices.length; i++) {
                int maxChoice = perSlotCanonicals.get(multiIndices.get(i)).size();
                choices[i]++;
                if (choices[i] < maxChoice) {
                    overflow = false;
                    break;
                }
                choices[i] = 0;
            }
            if (overflow) {
                break;
            }
        }

        return null;
    }

    // ====================== 生命周期 ======================

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        // 清空所有映射，由 BukkitRecipeRegister 在配方注册时自然重建
        fingerToRecipeKey.clear();
        recipeKeyToFingers.clear();
        concreteToCanonical.clear();
    }

}
