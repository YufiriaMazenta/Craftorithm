package pers.yufiria.craftorithm.recipe;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * 材料匹配模式
 */
public enum IngredientMatchMode {

    /**
     * 使用第一个识别到的物品id匹配材料（原有逻辑）
     */
    FIRST(
        (itemIds, item) -> {
            if (ItemHelper.isAir(item)) {
                return false;
            }
            Optional<NamespacedItemIdStack> itemIdStackOpt = ItemManager.INSTANCE.matchItemIdOrVanilla(item, true);
            return itemIdStackOpt.isPresent() && itemIds.contains(itemIdStackOpt.get().itemId());
        }
    ),
    /**
     * 收集物品所有可能的物品id匹配材料
     */
    ALL(
        (itemIds, item) -> {
            if (!item.hasItemMeta()) {
                return itemIds.contains(NamespacedItemId.fromMaterial(item.getType()));
            }
            //逐个物品源尝试并提前退出, 是否已有物品源认领了这个物品
            boolean claimed = false;
            for (Map.Entry<String, ItemProvider> itemProviderEntry : ItemManager.INSTANCE.itemProviders().entrySet()) {
                NamespacedItemId itemId;
                try {
                    itemId = itemProviderEntry.getValue().matchItemId(item);
                } catch (Throwable t) {
                    itemProviderEntry.getValue().logError(t);
                    continue;
                }
                if (itemId == null) {
                    continue;
                }
                claimed = true;
                if (itemIds.contains(itemId)) {
                    return true;
                }
            }
            //物品已被物品源认领时不能回退为原版物品, 否则自定义物品会顶替其基础材质参与原版材料匹配
            if (claimed) {
                return false;
            }
            return itemIds.contains(NamespacedItemId.fromMaterial(item.getType()));
        }
    );

    private final BiFunction<Collection<NamespacedItemId>, ItemStack, Boolean> containsImpl;

    IngredientMatchMode(BiFunction<Collection<NamespacedItemId>, ItemStack, Boolean> containsImpl) {
        this.containsImpl = containsImpl;
    }

    /**
     * 判断材料物品id集合是否包含指定物品
     * @param itemIds 材料物品id集合
     * @param item 要判断的物品
     * @return 包含返回true, 不包含或物品为空气返回false
     */
    public boolean containsItem(Collection<NamespacedItemId> itemIds, ItemStack item) {
        return containsImpl.apply(itemIds, item);
    }

}
