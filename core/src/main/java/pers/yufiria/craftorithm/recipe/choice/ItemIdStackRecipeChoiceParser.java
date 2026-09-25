package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.ItemGroup;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Objects;

public enum ItemIdStackRecipeChoiceParser implements RecipeChoiceParser {

    INSTANCE;

    @Override
    public RecipeChoice parse(String choiceStr) {
        if (choiceStr == null || choiceStr.isEmpty()) {
            throw new RecipeLoadException(choiceStr + " is not a valid ingredient ID.");
        }
        List<NamespacedItemId> itemIds;

        //该材料引用的物品组ID, 非物品组材料为null
        String itemGroupId = null;
        if (!choiceStr.contains(":")) {
            Material material;
            int amount;
            if (choiceStr.contains(" ")) {
                //如果包含数量，需要去除数量
                int spaceIndex = choiceStr.indexOf(" ");
                material = MaterialHelper.matchMaterial(choiceStr.substring(0, spaceIndex));
                amount = Integer.parseInt(choiceStr.substring(spaceIndex + 1));
            } else {
                material = MaterialHelper.matchMaterial(choiceStr);
                amount = 1;
            }
            if (material == null) {
                throw new RecipeLoadException(choiceStr + " is not a valid material");
            }
            itemIds = List.of(NamespacedItemId.fromMaterial(material));
            return new ItemIdStackRecipeChoice(itemIds, amount);
        } else {
            int amount;
            int index = choiceStr.indexOf(":");
            String namespace = choiceStr.substring(0, index);
            namespace = namespace.toLowerCase();
            switch (namespace) {
                case "minecraft":
                    Material material;
                    if (choiceStr.contains(" ")) {
                        //如果包含数量，需要去除数量
                        int spaceIndex = choiceStr.indexOf(" ");
                        material = MaterialHelper.matchMaterial(choiceStr.substring(0, spaceIndex));
                        amount = Integer.parseInt(choiceStr.substring(spaceIndex + 1));
                    } else {
                        material = MaterialHelper.matchMaterial(choiceStr);
                        amount = 1;
                    }
                    if (material == null) {
                        throw new RecipeLoadException(choiceStr + " is not a valid material");
                    }
                    itemIds = List.of(NamespacedItemId.fromMaterial(material));
                    break;
                case "tag":
                    StringAmount tagPart = splitAmount(choiceStr.substring(4));
                    Tag<Material> materialTag = IngredientUtils.getTag(tagPart.key())
                        .orElseThrow(() -> new RecipeLoadException(tagPart.key() + " is not a valid tag"));
                    amount = tagPart.amount();
                    itemIds = materialTag.getValues().stream()
                        .map(NamespacedItemId::fromMaterial)
                        .toList();
                    itemGroupId = ItemGroup.GROUP_TYPE_TAG + ":" + tagPart.key();
                    break;
                case "item_pack":
                    //是物品组
                    StringAmount packPart = splitAmount(choiceStr.substring("item_pack:".length()));
                    ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packPart.key());
                    if (itemPack == null) {
                        throw new RecipeLoadException(packPart.key() + " is not a valid item pack");
                    }
                    itemIds = itemPack.itemIds();
                    //物品组只记录物品id, 数量由引用处统一指定, 组内所有物品使用同一个数量
                    amount = packPart.amount();
                    itemGroupId = ItemGroup.GROUP_TYPE_ITEM_PACK + ":" + packPart.key();
                    break;
                default:
                    NamespacedItemIdStack namespacedItemIdStack = Objects.requireNonNull(
                        NamespacedItemIdStack.fromString(choiceStr),
                        "Cannot parse ingredient from string: " + choiceStr
                    );
                    itemIds = List.of(namespacedItemIdStack.itemId());
                    amount = namespacedItemIdStack.amount();
                    break;
            }
            return new ItemIdStackRecipeChoice(itemIds, amount, itemGroupId);
        }
    }

    /**
     * 剥离字符串尾部的数量后缀
     * @param keyStr 待剥离的字符串
     * @return 剥离数量后的字符串与数量, 没有数量后缀时数量为1
     */
    private static StringAmount splitAmount(String keyStr) {
        int spaceIndex = keyStr.lastIndexOf(' ');
        if (spaceIndex == -1) {
            return new StringAmount(keyStr, 1);
        }
        String amountStr = keyStr.substring(spaceIndex + 1);
        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            throw new RecipeLoadException(amountStr + " is not a valid amount");
        }
        return new StringAmount(keyStr.substring(0, spaceIndex), amount);
    }

    private record StringAmount(String key, int amount) {}
}
