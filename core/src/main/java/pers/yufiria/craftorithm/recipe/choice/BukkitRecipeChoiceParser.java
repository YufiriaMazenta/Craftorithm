package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.MaterialHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.Optional;

public enum BukkitRecipeChoiceParser implements RecipeChoiceParser {

    INSTANCE;

    @Override
    public RecipeChoice parse(String choiceStr) {
        RecipeChoice bukkitChoice;
        if (choiceStr == null || choiceStr.isEmpty()) {
            throw new RecipeLoadException(choiceStr + " is not a valid ingredient ID.");
        }
        if (!choiceStr.contains(":")) {
            Material material = Material.matchMaterial(choiceStr);
            if (material == null) {
                throw new RecipeLoadException(choiceStr + " is not a valid material");
            }
            bukkitChoice = new RecipeChoice.MaterialChoice(material);
        } else {
            int index = choiceStr.indexOf(":");
            String namespace = choiceStr.substring(0, index);
            namespace = namespace.toLowerCase();
            switch (namespace) {
                case "minecraft":
                    Material material = MaterialHelper.matchMaterial(choiceStr);
                    if (material == null) {
                        throw new RecipeLoadException(choiceStr + " is not a valid material");
                    }
                    bukkitChoice = new RecipeChoice.MaterialChoice(material);
                    break;
                case "tag":
                    String tagKeyStr = choiceStr.substring(4);
                    Optional<Tag<Material>> tagOpt = RecipeUtils.getTag(tagKeyStr);
                    if (tagOpt.isEmpty()) {
                        throw new RecipeLoadException(tagKeyStr + " is not a valid tag");
                    }
                    Tag<Material> materialTag = tagOpt.get();
                    bukkitChoice = new RecipeChoice.MaterialChoice(materialTag);
                    break;
                case "item_pack":
                    //是物品组
                    String packId = choiceStr.substring("item_pack:".length());
                    ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packId);
                    if (itemPack == null) {
                        throw new RecipeLoadException(packId + " is not a valid item pack");
                    }
                    boolean allVanilla = true;
                    for (NamespacedItemIdStack stackedItemId : itemPack.itemIds()) {
                        if (!stackedItemId.itemId().namespace().equals("minecraft")) {
                            allVanilla = false;
                            break;
                        }
                    }
                    if (allVanilla) {
                        bukkitChoice = new RecipeChoice.MaterialChoice(itemPack.items().stream().map(ItemStack::getType).toList());
                    } else {
                        bukkitChoice = new RecipeChoice.ExactChoice(itemPack.items());
                    }
                    break;
                default:
                    ItemStack item = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(choiceStr))
                        .orElseThrow(() -> new RecipeLoadException("Cannot parse item by choice name: " + choiceStr))
                        .clone();
                    item.setAmount(1);
                    bukkitChoice = new RecipeChoice.ExactChoice(item);
                    break;
            }
        }
        return bukkitChoice;
    }
}
