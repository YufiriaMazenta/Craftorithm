package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;

public enum ItemIdStackRecipeChoiceParser implements RecipeChoiceParser {

    INSTANCE;

    @Override
    public RecipeChoice parse(String choiceStr) {
        if (choiceStr == null || choiceStr.isEmpty()) {
            throw new RecipeLoadException(choiceStr + " is not a valid ingredient ID.");
        }
        List<NamespacedItemIdStack> choices;
        if (!choiceStr.contains(":")) {
            Material material;
            if (choiceStr.contains(" ")) {
                //如果包含数量，需要去除数量
                material = MaterialHelper.matchMaterial(choiceStr.substring(0, choiceStr.indexOf(" ")));
            } else {
                material = MaterialHelper.matchMaterial(choiceStr);
            }
            if (material == null) {
                throw new RecipeLoadException(choiceStr + " is not a valid material");
            }
            choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
            return new ItemIdStackRecipeChoice(choices);
        }
        int index = choiceStr.indexOf(":");
        String namespace = choiceStr.substring(0, index);
        namespace = namespace.toLowerCase();
        switch (namespace) {
            case "minecraft":
                Material material;
                if (choiceStr.contains(" ")) {
                    //如果包含数量，需要去除数量
                    material = MaterialHelper.matchMaterial(choiceStr.substring(0, choiceStr.indexOf(" ")));
                } else {
                    material = MaterialHelper.matchMaterial(choiceStr);
                }
                if (material == null) {
                    throw new RecipeLoadException(choiceStr + " is not a valid material");
                }
                choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
                break;
            case "tag":
                StringAmount tagPart = splitAmount(choiceStr.substring(4));
                Tag<Material> materialTag = IngredientUtils.getTag(tagPart.key())
                    .orElseThrow(() -> new RecipeLoadException(tagPart.key() + " is not a valid tag"));
                int tagAmount = tagPart.amount();
                choices = materialTag.getValues().stream()
                    .map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it), tagAmount))
                    .toList();
                break;
            case "item_pack":
                //是物品组
                StringAmount packPart = splitAmount(choiceStr.substring("item_pack:".length()));
                ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packPart.key());
                if (itemPack == null) {
                    throw new RecipeLoadException(packPart.key() + " is not a valid item pack");
                }
                //物品组只记录物品id, 数量由引用处统一指定, 组内所有物品使用同一个数量
                int packAmount = packPart.amount();
                choices = itemPack.itemIds().stream()
                    .map(itemId -> new NamespacedItemIdStack(itemId, packAmount))
                    .toList();
                break;
            default:
                choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
                break;
        }
        return new ItemIdStackRecipeChoice(choices);
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
