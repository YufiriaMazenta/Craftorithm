package pers.yufiria.craftorithm.recipe.util;

import crypticlib.util.MaterialHelper;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.lang.reflect.Field;
import java.util.Locale;

public class BukkitRecipeChoiceParser {

    public static RecipeChoice parseChoice(String choiceStr) {
        if (choiceStr == null || choiceStr.isEmpty()) {
            throw new RecipeLoadException(choiceStr + " is not a valid ingredient ID.");
        }
        if (!choiceStr.contains(":")) {
            Material material = Material.matchMaterial(choiceStr);
            if (material == null) {
                throw new RecipeLoadException(choiceStr + " is not a valid material");
            }
            return new RecipeChoice.MaterialChoice(material);
        }
        int index = choiceStr.indexOf(":");
        String namespace = choiceStr.substring(0, index);
        namespace = namespace.toLowerCase();
        switch (namespace) {
            case "minecraft":
                Material material = MaterialHelper.matchMaterial(choiceStr);
                if (material == null) {
                    throw new RecipeLoadException(choiceStr + " is not a valid material");
                }
                return new RecipeChoice.MaterialChoice(material);
            case "tag":
                String tagStr = choiceStr.substring(4).toUpperCase(Locale.ROOT);
                Tag<Material> materialTag;
                try {
                    Field field = Tag.class.getField(tagStr);
                    materialTag = (Tag<Material>) field.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RecipeLoadException(e);
                }
                return new RecipeChoice.MaterialChoice(materialTag);
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
                    return new RecipeChoice.MaterialChoice(itemPack.items().stream().map(ItemStack::getType).toList());
                } else {
                    return new RecipeChoice.ExactChoice(itemPack.items());
                }
            default:
                ItemStack item = ItemManager.INSTANCE.matchItem(NamespacedItemIdStack.fromString(choiceStr)).clone();
                item.setAmount(1);
                return new RecipeChoice.ExactChoice(item);
        }
    }
    
}
