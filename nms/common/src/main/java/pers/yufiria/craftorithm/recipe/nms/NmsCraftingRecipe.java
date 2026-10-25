package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.remainder.RemainderManager;
import pers.yufiria.craftorithm.recipe.remainder.RemainderRule;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 用于将nms的输入内容转化为{@link BukkitCraftingInput}
 * <p>
 * 有序与无序配方的版本实现都只需要实现这一个转换方法，其余取值与匹配由各自接口的默认方法提供
 */
public interface NmsCraftingRecipe<NmsInput> extends NmsRecipe<BukkitCraftingInput> {

    /**
     * 把 NMS 合成格逐格转换为 Bukkit 合成格（行优先展开）
     */
    BukkitCraftingInput toBukkitCraftingInput(NmsInput nmsInput);

    /**
     * 把配方配置中的剩余物逐槽应用到版本实现提供的列表
     * <p>
     * 没有匹配规则的槽位不会回调 setter，从而保留该版本的原版剩余物
     */
    default void applyRemainders(
        NmsInput nmsInput,
        BiConsumer<Integer, org.bukkit.inventory.ItemStack> setter
    ) {
        BukkitCraftingInput bukkitCraftingInput = toBukkitCraftingInput(nmsInput);
        List<RemainderRule> rules = RemainderManager.INSTANCE.getRules(recipeKey());
        if (rules.isEmpty()) {
            return;
        }

        List<ItemStack> items = bukkitCraftingInput.items();
        Map<Integer, ItemStack> remainders = new LinkedHashMap<>();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack item = items.get(slot);
            if (ItemHelper.isAir(item)) {
                continue;
            }
            for (RemainderRule rule : rules) {
                if (!rule.matches(item)) {
                    continue;
                }
                remainders.put(slot, rule.action().createRemainder(item));
                break;
            }
        }
        remainders.forEach(setter);
    }

    /**
     * 供没有原生默认剩余物辅助方法的旧版本(1.20-1.21)使用
     */
    default void applyDefaultRemainders(
        NmsInput nmsInput,
        BiConsumer<Integer, org.bukkit.inventory.ItemStack> setter
    ) {
        List<ItemStack> items = toBukkitCraftingInput(nmsInput).items();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack item = items.get(slot);
            if (ItemHelper.isAir(item)) {
                continue;
            }
            Material remainderMaterial = item.getType().getCraftingRemainingItem();
            if (remainderMaterial == null || remainderMaterial.isAir()) {
                continue;
            }
            setter.accept(slot, new ItemStack(remainderMaterial));
        }
    }

}
