package pers.yufiria.craftorithm.item;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.*;

public class ItemPack {

    private final String packId;
    private final List<ItemStack> items;
    private final List<NamespacedItemIdStack> itemIds;

    public ItemPack(String packId, List<String> itemIdStrList) {
        this.packId = packId;
        itemIds = new ArrayList<>();
        for (String itemIdStr : itemIdStrList) {
            NamespacedItemIdStack itemIdStack = NamespacedItemIdStack.fromString(itemIdStr);
            if (itemIdStack == null) {
                continue;
            }
            switch (itemIdStack.itemId().namespace()) {
                case "tag" -> {
                    //是一个tag,将tag的所有物品加入
                    String tagId = itemIdStack.itemId().toString();
                    Optional<Tag<Material>> tagOpt = IngredientUtils.getTag(tagId);
                    if (tagOpt.isEmpty()) {
                        throw new RecipeLoadException(tagId + " is not a valid tag");
                    }
                    Tag<Material> materialTag = tagOpt.get();
                    for (Material material : materialTag.getValues()) {
                        this.itemIds.add(new NamespacedItemIdStack(NamespacedItemId.fromMaterial(material), itemIdStack.amount()));
                    }
                }
                case "item_pack" -> {
                    //是另一个物品组,将他的所有物品加入
                    String itemPackId = itemIdStack.itemId().toString();
                    ItemPack otherItemPack = ItemManager.INSTANCE.getItemPack(itemPackId);
                    if (otherItemPack == null) {
                        continue;
                    }
                    this.itemIds.addAll(otherItemPack.itemIds);
                }
                default -> {
                    this.itemIds.add(itemIdStack);

                }
            }
        }
        this.items = new ArrayList<>();
        for (NamespacedItemIdStack itemId : itemIds) {
            ItemManager.INSTANCE.matchItem(itemId).ifPresent(item -> items.add(item.clone()));
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
