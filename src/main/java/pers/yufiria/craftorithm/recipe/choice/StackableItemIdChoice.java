package pers.yufiria.craftorithm.recipe.choice;

import crypticlib.util.ItemHelper;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.ItemPack;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.exception.RecipeLoadException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StackableItemIdChoice implements RecipeChoice {

    private final List<NamespacedItemIdStack> itemIds;
    private final Random rand = new Random();

    public StackableItemIdChoice(String choiceStr) {
        if (choiceStr == null || choiceStr.isEmpty()) {
            throw new RecipeLoadException(choiceStr + " is not a valid ingredient ID.");
        }
        if (!choiceStr.contains(":")) {
            Material material = Material.matchMaterial(choiceStr);
            if (material == null) {
                throw new RecipeLoadException(choiceStr + " is not a valid material");
            }
            this.itemIds = List.of(NamespacedItemIdStack.fromString(choiceStr));
            return;
        }
        int index = choiceStr.indexOf(":");
        String namespace = choiceStr.substring(0, index);
        namespace = namespace.toLowerCase();
        switch (namespace) {
            case "minecraft":
                Material material = Material.matchMaterial(choiceStr);
                if (material == null) {
                    throw new RecipeLoadException(choiceStr + " is not a valid material");
                }
                this.itemIds = List.of(NamespacedItemIdStack.fromString(choiceStr));
                break;
            case "tag":
                String tagStr = choiceStr.substring(4).toUpperCase(Locale.ROOT);
                Tag<Material> materialTag;
                try {
                    Field field = Tag.class.getField(tagStr);
                    materialTag = (Tag<Material>) field.get(null);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RecipeLoadException(e);
                }

                int spaceIndex = tagStr.lastIndexOf(' ');
                if (spaceIndex == -1) {
                    this.itemIds = materialTag.getValues().stream().map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it))).toList();
                } else {
                    String tagId = tagStr.substring(0, spaceIndex);
                    int amount = Integer.parseInt(tagStr.substring(spaceIndex + 1));
                    this.itemIds = materialTag.getValues().stream().map(it -> new NamespacedItemIdStack(NamespacedItemId.fromMaterial(it), amount)).toList();
                }
                break;
            case "item_pack":
                //是物品组
                String packId = choiceStr.substring("item_pack:".length());
                ItemPack itemPack = ItemManager.INSTANCE.getItemPack(packId);
                if (itemPack == null) {
                    throw new RecipeLoadException(packId + " is not a valid item pack");
                }
                this.itemIds = itemPack.itemIds();
                break;
            default:
                this.itemIds = List.of(NamespacedItemIdStack.fromString(choiceStr));
                break;
        }
    }

    public StackableItemIdChoice(List<NamespacedItemIdStack> itemIds) {
        if (itemIds == null || itemIds.isEmpty())
            throw new UnsupportedOperationException("ItemIds cannot be null or empty");
        this.itemIds = itemIds;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return ItemManager.INSTANCE.matchItem(itemIds.get(rand.nextInt(itemIds.size())));
    }

    @Override
    public @NotNull RecipeChoice clone() {
        return new StackableItemIdChoice(itemIds);
    }

    public int getUseAmount(NamespacedItemId itemId) {
        for (NamespacedItemIdStack stackedItemId : itemIds) {
            if (stackedItemId.itemId().equals(itemId)) {
                return stackedItemId.amount();
            }
        }
        throw new IllegalArgumentException("Do not have this item id: " + itemId);
    }

    @Override
    public boolean test(@NotNull ItemStack itemStack) {
        NamespacedItemIdStack stackedItemId = ItemManager.INSTANCE.matchItemId(itemStack, true);
        if (stackedItemId == null) {
            stackedItemId = new NamespacedItemIdStack(NamespacedItemId.fromMaterial(itemStack.getType()), itemStack.getAmount());
        }
        NamespacedItemIdStack finalStackedItemId = stackedItemId;
        return itemIds.stream().anyMatch(itemId -> itemId.isSimilar(finalStackedItemId) && finalStackedItemId.amount() >= itemId.amount());
    }

    @Override
    public @NotNull RecipeChoice validate(boolean allowEmptyRecipes) {
        if (this.itemIds.stream().anyMatch((it) -> {
            ItemStack itemStack = ItemManager.INSTANCE.matchItem(it);
            return ItemHelper.isAir(itemStack);
        })) {
            throw new IllegalArgumentException("RecipeChoice.ExactChoice cannot contain air");
        } else {
            return this;
        }
    }



}
