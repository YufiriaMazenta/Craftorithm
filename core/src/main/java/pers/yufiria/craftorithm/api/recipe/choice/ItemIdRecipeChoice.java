package pers.yufiria.craftorithm.api.recipe.choice;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemIdRecipeChoice implements CustomRecipeChoice {

    private final RecipeChoice bukkitChoice;
    private List<NamespacedItemId> ingredients;

    public ItemIdRecipeChoice(RecipeChoice bukkitChoice) {
        this.bukkitChoice = bukkitChoice;
        if (bukkitChoice instanceof MaterialChoice materialChoice) {
            ingredients = materialChoice.getChoices().stream().map(NamespacedItemId::fromMaterial).collect(Collectors.toList());
        } else if (bukkitChoice instanceof ExactChoice exactChoice) {
            ingredients = exactChoice.getChoices().stream().map(item -> {
                NamespacedItemIdStack namespacedItemIdStack = ItemManager.INSTANCE.matchItemIdOrCreate(item, true);
                return namespacedItemIdStack.itemId();
            }).collect(Collectors.toList());
        }
    }

    @Override
    public RecipeChoice bukkitChoice() {
        return bukkitChoice;
    }

    @Override
    public @NotNull ItemStack getItemStack() {
        return bukkitChoice.getItemStack();
    }

    @Override
    public @NotNull RecipeChoice clone() {
        return new ItemIdRecipeChoice(
            bukkitChoice.clone()
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
