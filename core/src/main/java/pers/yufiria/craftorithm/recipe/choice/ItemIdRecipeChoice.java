package pers.yufiria.craftorithm.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.item.groupitem.ItemGroupItemManager;

import java.util.*;
import java.util.stream.Collectors;

public class ItemIdRecipeChoice implements CustomRecipeChoice {

    private final RecipeChoice bukkitChoice;
    private final Collection<NamespacedItemId> ingredients;
    /**
     * 该材料引用的物品组ID, 如tag:minecraft:planks或item_pack:my_pack, 非物品组材料为null
     */
    private final @Nullable String itemGroupSource;

    public ItemIdRecipeChoice(RecipeChoice bukkitChoice) {
        this(bukkitChoice, null);
    }

    public ItemIdRecipeChoice(RecipeChoice bukkitChoice, @Nullable String itemGroupSource) {
        this.bukkitChoice = bukkitChoice;
        this.itemGroupSource = itemGroupSource;
        Collection<NamespacedItemId> ingredients;
        if (bukkitChoice instanceof MaterialChoice materialChoice) {
            ingredients = materialChoice.getChoices().stream().map(NamespacedItemId::fromMaterial).collect(Collectors.toList());
        } else if (bukkitChoice instanceof ExactChoice exactChoice) {
            ingredients = exactChoice.getChoices().stream().map(item -> {
                NamespacedItemIdStack namespacedItemIdStack = ItemManager.INSTANCE.matchItemIdOrCreate(item, true);
                return Objects.requireNonNull(namespacedItemIdStack).itemId();
            }).collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Cannot use " + bukkitChoice.getClass().getName() + " as a bukkit recipe choice");
        }
        if (ingredients.size() > PluginConfigs.INGREDIENT_USE_SET_THRESHOLD.value()) {
            this.ingredients = Set.copyOf(ingredients);
        } else {
            this.ingredients = List.copyOf(ingredients);
        }
    }

    @Override
    public RecipeChoice bukkitChoice() {
        return bukkitChoice;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        //物品组材料使用构建出的物品组占位符作为展示物品
        if (itemGroupSource != null) {
            Optional<ItemStack> itemGroupItem = ItemGroupItemManager.INSTANCE.buildItemGroupItem(itemGroupSource);
            if (itemGroupItem.isPresent()) {
                return itemGroupItem.get();
            }
        }
        return bukkitChoice.getItemStack();
    }

    @Override
    public @NotNull RecipeChoice clone() {
        return new ItemIdRecipeChoice(
            bukkitChoice.clone(),
            itemGroupSource
        );
    }

    @Override
    public boolean test(@NotNull ItemStack itemStack) {
        Optional<NamespacedItemIdStack> inputItemIdStackOpt = ItemManager.INSTANCE.matchItemIdOrVanilla(itemStack, true);
        if (inputItemIdStackOpt.isEmpty()) {
            return false;
        }
        NamespacedItemIdStack itemIdStack = inputItemIdStackOpt.get();
        return ingredients.contains(itemIdStack.itemId());
    }

}
