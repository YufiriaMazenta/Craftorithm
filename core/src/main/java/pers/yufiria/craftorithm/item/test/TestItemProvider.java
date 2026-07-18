package pers.yufiria.craftorithm.item.test;

import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.ItemProvider;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

public enum TestItemProvider implements ItemProvider {

    INSTANCE;

    private final NamespacedKey ITEM_ID_KEY = new NamespacedKey("test", "item_id");

    @Override
    public @NotNull String namespace() {
        return "test";
    }

    @Override
    public @Nullable NamespacedItemIdStack matchItemId(ItemStack itemStack, boolean ignoreAmount) {
        if (ItemHelper.isAir(itemStack)) {
            return null;
        }
        PersistentDataContainer dataContainer = itemStack.getItemMeta().getPersistentDataContainer();
        String itemKey = dataContainer.get(ITEM_ID_KEY, PersistentDataType.STRING);
        if (itemKey == null) {
            return null;
        }
        if (ignoreAmount) {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemKey
                ),
                1
            );
        } else {
            return new NamespacedItemIdStack(
                new NamespacedItemId(
                    namespace(),
                    itemKey
                ),
                itemStack.getAmount()
            );
        }
    }

    @Override
    public @Nullable ItemStack matchItem(String itemId) {
        ItemStack itemStack = new ItemStack(Material.IRON_INGOT);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(ITEM_ID_KEY, PersistentDataType.STRING, itemId);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
