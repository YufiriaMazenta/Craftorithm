package pers.yufiria.craftorithm.recipe.ingredient;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * 配方材料匹配器
 */
public interface ItemIdIngredientMatcher {

    Collection<NamespacedItemId> itemIds();
    
    default boolean matches(@NotNull ItemStack item) {
        if (PluginConfigs.INGREDIENT_TRY_ALL_POSSIBLE_ITEM_ID.value()) {
            if (!item.hasItemMeta()) {
                return itemIds().contains(NamespacedItemId.fromMaterial(item.getType()));
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
                if (itemIds().contains(itemId)) {
                    return true;
                }
            }
            //物品已被物品源认领时不能回退为原版物品, 否则自定义物品会顶替其基础材质参与原版材料匹配
            if (claimed) {
                return false;
            }
            return itemIds().contains(NamespacedItemId.fromMaterial(item.getType()));
        } else {
            if (ItemHelper.isAir(item)) {
                return false;
            }
            Optional<NamespacedItemIdStack> itemIdStackOpt = ItemManager.INSTANCE.matchItemIdOrVanilla(item, true);
            return itemIdStackOpt.isPresent() && itemIds().contains(itemIdStackOpt.get().itemId());
        }
    }

}
