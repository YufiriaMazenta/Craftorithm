package pers.yufiria.craftorithm.item;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.exception.ItemNotFoundException;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.*;
import java.util.function.Function;

public class ItemPack {

    private final String packId;
    private final List<ItemStack> items;
    private final List<NamespacedItemId> itemIds;

    public ItemPack(String packId, List<String> itemIdStrList) {
        this(packId, itemIdStrList, ItemManager.INSTANCE::getItemPack);
    }

    /**
     * @param packResolver 解析「物品组引用」的解析器。重载时传入本轮正在构造的表, 保证同轮内的引用可见,
     *                     同时避免为了可见性而提前污染全局表导致半加载状态
     */
    public ItemPack(String packId, List<String> itemIdStrList, @NotNull Function<String, ItemPack> packResolver) {
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
                case ItemGroup.GROUP_TYPE_TAG -> {
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
                case ItemGroup.GROUP_TYPE_ITEM_PACK -> {
                    //是另一个物品组,将他的所有物品加入
                    ItemPack otherItemPack = packResolver.apply(itemId.itemId());
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