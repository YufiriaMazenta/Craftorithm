package pers.yufiria.craftorithm.item;

import crypticlib.chat.BukkitMsgSender;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ItemPack {

    private final String packId;
    private final List<ItemStack> items;
    private final List<NamespacedItemIdStack> itemIds;

    public ItemPack(String packId, List<NamespacedItemIdStack> itemIds) {
        this.packId = packId;
        this.itemIds = itemIds;
        this.items = new ArrayList<>();
        for (NamespacedItemIdStack itemId : itemIds) {
            try {
                ItemStack item = ItemManager.INSTANCE.matchItem(itemId).clone();
                items.add(item);
            } catch (ItemNotFoundException e) {
                BukkitMsgSender.INSTANCE.info("&cItem Pack Init Error | " + e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public List<ItemStack> items() {
        return Collections.unmodifiableList(items);
    }

    public List<NamespacedItemIdStack> itemIds() {
        return Collections.unmodifiableList(itemIds);
    }

    public String packId() {
        return packId;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemPack itemPack)) return false;

        return Objects.equals(packId, itemPack.packId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(packId);
    }

}
