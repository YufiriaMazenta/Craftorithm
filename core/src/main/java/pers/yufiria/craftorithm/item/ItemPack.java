package pers.yufiria.craftorithm.item;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.*;

public class ItemPack {

    private final String packId;
    private final List<ItemStack> items;
    private final List<NamespacedItemId> itemIds;

    public ItemPack(String packId, List<String> itemIdStrList) {
        this.packId = packId;
        itemIds = new ArrayList<>();
        for (String itemIdStr : itemIdStrList) {
            //物品组只记录物品id, 条目上的数量后缀会被忽略
            //这里沿用带数量的解析方式, 是为了正确剥离数量后缀后再取物品id
            NamespacedItemIdStack parsedItemId = NamespacedItemIdStack.fromString(itemIdStr);
            if (parsedItemId == null) {
                continue;
            }
            NamespacedItemId itemId = parsedItemId.itemId();
            switch (itemId.namespace()) {
                case "tag" -> {
                    //是一个tag,将tag的所有物品加入
                    String tagId = itemId.itemId();
                    Optional<Tag<Material>> tagOpt = IngredientUtils.getTag(tagId);
                    if (tagOpt.isEmpty()) {
                        throw new ItemNotFoundException(tagId + " is not a valid tag");
                    }
                    Tag<Material> materialTag = tagOpt.get();
                    for (Material material : materialTag.getValues()) {
                        this.itemIds.add(NamespacedItemId.fromMaterial(material));
                    }
                }
                case "item_pack" -> {
                    //是另一个物品组,将他的所有物品加入
                    ItemPack otherItemPack = ItemManager.INSTANCE.getItemPack(itemId.itemId());
                    if (otherItemPack == null) {
                        continue;
                    }
                    this.itemIds.addAll(otherItemPack.itemIds);
                }
                default -> {
                    this.itemIds.add(itemId);
                }
            }
        }
        this.items = new ArrayList<>();
        for (NamespacedItemId itemId : itemIds) {
            ItemManager.INSTANCE.matchItem(itemId).ifPresent(item -> items.add(item.clone()));
        }
    }

    public List<ItemStack> items() {
        return Collections.unmodifiableList(items);
    }

    public List<NamespacedItemId> itemIds() {
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