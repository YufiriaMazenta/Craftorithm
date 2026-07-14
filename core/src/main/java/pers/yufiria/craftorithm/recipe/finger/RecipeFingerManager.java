package pers.yufiria.craftorithm.recipe.finger;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.CollectionsUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配方指纹管理器。<p>
 * 通过规范指纹（canonical finger）实现基于 Craftorithm 物品 ID 的配方快速查找，
 * 替代 Bukkit 原生匹配，支持 tag / itempack 等多物品材料。
 */
@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE, priority = 3),
        @TaskRule(lifeCycle = LifeCycle.RELOAD, priority = 3),
        @TaskRule(lifeCycle = LifeCycle.DISABLE)
    }
)
public enum RecipeFingerManager implements LifeCycleTask {

    INSTANCE;

    private static final int MAX_CANDIDATE_COMBINATIONS = 4096;

    /** 规范指纹 → 按注册顺序保存的配方 Key（后注册优先） */
    private final Map<String, List<NamespacedKey>> fingerToRecipeKeys = new ConcurrentHashMap<>();

    /** 配方 Key → 它注册的所有规范指纹（卸载时清理用） */
    private final Map<NamespacedKey, List<String>> recipeKeyToFingers = new ConcurrentHashMap<>();

    /** 配方 Key → 它持有的 canonical 反查映射 */
    private final Map<NamespacedKey, Set<RecipeFingerGenerator.CanonicalMapping>> recipeKeyToCanonicalMappings = new ConcurrentHashMap<>();

    /** canonical 反查映射引用计数，避免移除一个配方时影响其他配方 */
    private final Map<RecipeFingerGenerator.CanonicalMapping, Integer> canonicalMappingReferenceCounts = new ConcurrentHashMap<>();

    /**
     * 具体物品 ID → 它所属的规范形式。<p>
     * 一个物品可能同时属于多个 tag / itempack，所以 value 是 Set。
     */
    private final Map<String, Set<String>> concreteToCanonical = new ConcurrentHashMap<>();

    // ====================== 注册 / 注销 ======================

    /**
     * 根据配方配置构建并注册指纹。
     *
     * @param recipeKey 配方 NamespacedKey
     * @param recipeConfig 配方 YAML 配置
     */
    public void registerRecipeFinger(NamespacedKey recipeKey, YamlConfiguration recipeConfig) {
        unregisterRecipeFinger(recipeKey);

        RecipeFingerGenerator.GenerationResult generationResult = RecipeFingerGenerator.generate(recipeConfig);
        List<String> fingers = generationResult.fingers().stream()
            .distinct()
            .toList();
        if (fingers.isEmpty()) {
            return;
        }

        Set<RecipeFingerGenerator.CanonicalMapping> canonicalMappings = generationResult.canonicalMappings();
        for (RecipeFingerGenerator.CanonicalMapping canonicalMapping : canonicalMappings) {
            registerCanonicalMapping(canonicalMapping);
        }
        recipeKeyToCanonicalMappings.put(recipeKey, canonicalMappings);

        for (String finger : fingers) {
            registerFinger(finger, recipeKey);
        }
        recipeKeyToFingers.put(recipeKey, fingers);
    }

    private void registerFinger(String finger, NamespacedKey recipeKey) {
        fingerToRecipeKeys.compute(finger, (ignored, recipeKeys) -> {
            List<NamespacedKey> updated = recipeKeys == null ? new ArrayList<>() : new ArrayList<>(recipeKeys);
            updated.remove(recipeKey);
            updated.add(recipeKey);
            return List.copyOf(updated);
        });
    }

    /**
     * 注销一个配方的所有指纹和 canonical 反查映射。
     */
    public void unregisterRecipeFinger(NamespacedKey recipeKey) {
        List<String> fingers = recipeKeyToFingers.remove(recipeKey);
        if (fingers != null) {
            for (String finger : fingers) {
                fingerToRecipeKeys.computeIfPresent(finger, (ignored, recipeKeys) -> {
                    List<NamespacedKey> updated = new ArrayList<>(recipeKeys);
                    updated.remove(recipeKey);
                    return updated.isEmpty() ? null : List.copyOf(updated);
                });
            }
        }

        Set<RecipeFingerGenerator.CanonicalMapping> canonicalMappings = recipeKeyToCanonicalMappings.remove(recipeKey);
        if (canonicalMappings != null) {
            canonicalMappings.forEach(this::unregisterCanonicalMapping);
        }
    }

    // ====================== 反查表 ======================

    private void registerCanonicalMapping(RecipeFingerGenerator.CanonicalMapping canonicalMapping) {
        canonicalMappingReferenceCounts.merge(canonicalMapping, 1, Integer::sum);
        concreteToCanonical.computeIfAbsent(canonicalMapping.concreteId(), ignored -> ConcurrentHashMap.newKeySet())
            .add(canonicalMapping.canonicalForm());
    }

    private void unregisterCanonicalMapping(RecipeFingerGenerator.CanonicalMapping canonicalMapping) {
        canonicalMappingReferenceCounts.computeIfPresent(canonicalMapping, (ignored, referenceCount) -> {
            if (referenceCount > 1) {
                return referenceCount - 1;
            }

            concreteToCanonical.computeIfPresent(canonicalMapping.concreteId(), (concreteId, canonicalForms) -> {
                canonicalForms.remove(canonicalMapping.canonicalForm());
                return canonicalForms.isEmpty() ? null : canonicalForms;
            });
            return null;
        });
    }

    // ====================== 查询 ======================

    /**
     * 根据合成网格物品查找配方 Key。
     *
     * @param matrix 合成网格（2x2 或 3x3，可能包含 null）
     * @return 匹配到的配方 Key，未匹配返回 null
     */
    public @Nullable NamespacedKey findRecipeByGrid(ItemStack[] matrix) {
        if (matrix == null || matrix.length == 0) {
            return null;
        }

        List<List<String>> grid = toConcreteIdGrid(matrix);
        if (grid.isEmpty()) {
            return null;
        }

        CollectionsUtils.trimEmptyBorders(grid, Objects::isNull);
        if (grid.isEmpty()) {
            return null;
        }

        return findWithCandidateExpansion(grid);
    }

    private List<List<String>> toConcreteIdGrid(ItemStack[] matrix) {
        int cols = (int) Math.sqrt(matrix.length);
        if (cols == 0 || cols * cols != matrix.length) {
            return List.of();
        }

        List<List<String>> grid = new ArrayList<>();
        for (int row = 0; row < cols; row++) {
            List<String> rowList = new ArrayList<>();
            for (int col = 0; col < cols; col++) {
                ItemStack item = matrix[row * cols + col];
                if (ItemHelper.isAir(item)) {
                    rowList.add(null);
                    continue;
                }

                NamespacedItemIdStack idStack = ItemManager.INSTANCE.matchItemId(item, true).orElse(null);
                String concreteId = idStack != null
                    ? idStack.itemId().toString()
                    : NamespacedItemId.fromMaterial(item.getType()).toString();
                rowList.add(concreteId);
            }
            grid.add(rowList);
        }
        return grid;
    }

    /**
     * 对每个非空格子尝试“具体 ID + 所有规范形式”。<p>
     * 具体 ID 永远先于 tag / itempack，避免更具体配方被宽泛材料覆盖。
     */
    private @Nullable NamespacedKey findWithCandidateExpansion(List<List<String>> grid) {
        List<SlotCandidate> slots = collectSlotCandidates(grid);
        if (slots.isEmpty()) {
            return null;
        }

        String[] choices = new String[slots.size()];
        int[] checkedCombinations = {0};
        return findWithCandidateExpansion(slots, choices, 0, checkedCombinations);
    }

    private List<SlotCandidate> collectSlotCandidates(List<List<String>> grid) {
        List<SlotCandidate> slots = new ArrayList<>();
        for (int row = 0; row < grid.size(); row++) {
            List<String> rowList = grid.get(row);
            for (int col = 0; col < rowList.size(); col++) {
                String concreteId = rowList.get(col);
                if (concreteId == null) {
                    continue;
                }
                slots.add(new SlotCandidate(row, col, candidateForms(concreteId)));
            }
        }
        return slots;
    }

    private List<String> candidateForms(String concreteId) {
        LinkedHashSet<String> forms = new LinkedHashSet<>();
        forms.add(concreteId);

        Set<String> canonicals = concreteToCanonical.get(concreteId);
        if (canonicals != null && !canonicals.isEmpty()) {
            canonicals.stream()
                .sorted()
                .forEach(forms::add);
        }
        return List.copyOf(forms);
    }

    private @Nullable NamespacedKey findWithCandidateExpansion(
        List<SlotCandidate> slots,
        String[] choices,
        int slotIndex,
        int[] checkedCombinations
    ) {
        if (checkedCombinations[0] >= MAX_CANDIDATE_COMBINATIONS) {
            return null;
        }

        if (slotIndex >= slots.size()) {
            checkedCombinations[0]++;
            NamespacedKey key = findRecipeKey(buildShapedFinger(slots, choices));
            if (key != null) {
                return key;
            }
            return findRecipeKey(buildShapelessFinger(choices));
        }

        for (String candidate : slots.get(slotIndex).candidates()) {
            choices[slotIndex] = candidate;
            NamespacedKey key = findWithCandidateExpansion(slots, choices, slotIndex + 1, checkedCombinations);
            if (key != null) {
                return key;
            }
        }
        return null;
    }

    private String buildShapedFinger(List<SlotCandidate> slots, String[] choices) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) {
                sb.append('|');
            }
            SlotCandidate slot = slots.get(i);
            sb.append('<')
                .append(slot.row())
                .append(',')
                .append(slot.col())
                .append(',')
                .append(choices[i])
                .append('>');
        }
        return sb.toString();
    }

    private String buildShapelessFinger(String[] choices) {
        List<String> sorted = new ArrayList<>(List.of(choices));
        sorted.sort(null);
        return String.join("|", sorted);
    }

    private @Nullable NamespacedKey findRecipeKey(String finger) {
        List<NamespacedKey> recipeKeys = fingerToRecipeKeys.get(finger);
        if (recipeKeys == null || recipeKeys.isEmpty()) {
            return null;
        }

        for (int i = recipeKeys.size() - 1; i >= 0; i--) {
            NamespacedKey recipeKey = recipeKeys.get(i);
            if (RecipeManager.INSTANCE.getRecipe(recipeKey) != null) {
                return recipeKey;
            }
        }
        return null;
    }

    private record SlotCandidate(int row, int col, List<String> candidates) {}

    // ====================== 生命周期 ======================

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        // 清空所有映射，由 BukkitRecipeRegister 在配方注册时自然重建
        fingerToRecipeKeys.clear();
        recipeKeyToFingers.clear();
        recipeKeyToCanonicalMappings.clear();
        canonicalMappingReferenceCounts.clear();
        concreteToCanonical.clear();
    }

}
