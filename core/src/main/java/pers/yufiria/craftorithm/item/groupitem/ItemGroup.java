package pers.yufiria.craftorithm.item.groupitem;

import pers.yufiria.craftorithm.item.NamespacedItemId;

import java.util.ArrayList;
import java.util.List;

public record ItemGroup(
    String groupType,
    String groupId,
    List<NamespacedItemId> items
) {

    public ItemGroup(String groupType, String groupId) {
        this(groupType, groupId, new ArrayList<>());
    }

    /**
     * 该物品组在配方材料里引用的ID, 例如 tag:minecraft:planks 或 item_pack:my_pack
     */
    public String toIngredientId() {
        return groupType + ":" + groupId;
    }

}
