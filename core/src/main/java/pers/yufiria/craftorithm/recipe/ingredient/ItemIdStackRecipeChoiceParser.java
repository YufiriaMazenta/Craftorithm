package pers.yufiria.craftorithm.recipe.ingredient;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.*;
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
        //没有命名空间时, 整个字符串就是一个原版材料
        if (!choiceStr.contains(":")) {
            MaterialAmount material = parseMaterial(choiceStr);
            return new ItemIdStackRecipeChoice(
                List.of(NamespacedItemId.fromMaterial(material.material())),
                material.amount()
            );
        }

        //该材料引用的物品组ID, 非物品组材料为null
        String itemGroupId = null;
        List<NamespacedItemId> itemIds;
        int amount;
        int index = choiceStr.indexOf(":");
        String namespace = choiceStr.substring(0, index).toLowerCase();
        switch (namespace) {
            case "minecraft":
                MaterialAmount material = parseMaterial(choiceStr);
                itemIds = List.of(NamespacedItemId.fromMaterial(material.material()));
                amount = material.amount();
                break;
            case ItemGroup.GROUP_TYPE_TAG:
                StringAmount tagPart = splitAmount(choiceStr.substring(index + 1));
                Tag<Material> materialTag = IngredientUtils.getTag(tagPart.key())
                    .orElseThrow(() -> new RecipeLoadException(tagPart.key() + " is not a valid tag"));
                itemIds = materialTag.getValues().stream()
                    .map(NamespacedItemId::fromMaterial)
                    .toList();
                amount = tagPart.amount();
                itemGroupId = ItemGroup.GROUP_TYPE_TAG + ":" + tagPart.key();
                break;
            case ItemGroup.GROUP_TYPE_ITEM_PACK:
                //是物品组
                StringAmount packPart = splitAmount(choiceStr.substring(index + 1));
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

    /**
     * 解析「材料id [数量]」形式的原版材料, 例如 {@code stone} 或 {@code stone 8}
     * @param choiceStr 材料字符串
     * @return 材料与数量, 没有数量后缀时数量为1
     * @throws RecipeLoadException 材料id不合法时抛出
     */
    private static MaterialAmount parseMaterial(String choiceStr) {
        int spaceIndex = choiceStr.indexOf(' ');
        Material material;
        int amount;
        if (spaceIndex == -1) {
            material = MaterialHelper.matchMaterial(choiceStr);
            amount = 1;
        } else {
            //如果包含数量，需要去除数量
            material = MaterialHelper.matchMaterial(choiceStr.substring(0, spaceIndex));
            amount = Integer.parseInt(choiceStr.substring(spaceIndex + 1));
        }
        if (material == null) {
            throw new RecipeLoadException(choiceStr + " is not a valid material");
        }
        return new MaterialAmount(material, amount);
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

    private record MaterialAmount(Material material, int amount) {}

    private record StringAmount(String key, int amount) {}
}
