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
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                String tagKeyStr = choiceStr.substring(4);
                int spaceIndex = tagKeyStr.lastIndexOf(' ');
                if (spaceIndex == -1) {
                    Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
                    if (tagOpt.isEmpty()) {
                        throw new RecipeLoadException(tagKeyStr + " is not a valid tag");
                    }
                    Tag<Material> materialTag = tagOpt.get();
                    choices = materialTag.getValues().stream().map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it))).collect(Collectors.toList());
                } else {
                    int amount = Integer.parseInt(tagKeyStr.substring(spaceIndex + 1));
                    tagKeyStr = tagKeyStr.substring(0, spaceIndex);
                    Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
                    if (tagOpt.isEmpty()) {
                        throw new RecipeLoadException(tagKeyStr + " is not a valid tag");
                    }
                    Tag<Material> materialTag = tagOpt.get();
                    choices = materialTag.getValues().stream().map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it), amount)).collect(Collectors.toList());
                }
                break;
            case "item_pack":
                //是物品组
                String packId = choiceStr.substring("item_pack:".length());
                ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packId);
                if (itemPack == null) {
                    throw new RecipeLoadException(packId + " is not a valid item pack");
                }
                choices = new ArrayList<>(itemPack.itemIds());
                break;
            default:
                choices = List.of(NamespacedItemIdStack.fromString(choiceStr));
                break;
        }
        return new ItemIdStackRecipeChoice(choices);
    }
}
