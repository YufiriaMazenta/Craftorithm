package pers.yufiria.craftorithm.recipe.crafting;

import crypticlib.util.ItemHelper;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.item.StackedItemId;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class CraftInput {

    public static final CraftInput EMPTY = new CraftInput();

    private final List<List<StackedItemId>> inputs;
    private Integer inputCount;
    private Integer width, height;

    private CraftInput() {
        this.inputs = new ArrayList<>();
        inputCount = 0;
        width = 0;
        height = 0;
    }

    public CraftInput(CraftingInventory craftingInventory) {
        @Nullable ItemStack[] matrix = craftingInventory.getMatrix();
        inputs = toInputs(matrix);
        width = inputs.get(0).size();
        height = inputs.size();
    }

    public CraftInput(ItemStack[] matrix) {
        if (matrix == null || matrix.length == 0) {
            inputs = new ArrayList<>();
            return;
        }
        if (matrix.length != 4 && matrix.length != 9) {
            throw new IllegalArgumentException("Matrix length must be 4 or 9");
        }
        inputs = toInputs(matrix);
    }

    public List<List<StackedItemId>> toInputs(ItemStack[] items) {
        this.inputCount = 0;
        if (items == null || items.length == 0) {
            return new ArrayList<>();
        }
        List<List<StackedItemId>> ingredients = new ArrayList<>();
        int sqrt = (int) Math.sqrt(items.length);
        for (int i = 0; i < sqrt; i++) {
            List<StackedItemId> line = new ArrayList<>();
            for (int j = 0; j < sqrt; j++) {
                int index = i * sqrt + j;
                ItemStack item = items[index];
                if (ItemHelper.isAir(item)) {
                    line.add(null);
                } else {
                    NamespacedItemId itemId = ItemManager.INSTANCE.matchItemId(item);
                    if (itemId == null) {
                        itemId = new NamespacedItemId(item.getType().getKey().getNamespace(), item.getType().getKey().getKey());
                    }
                    this.inputCount ++;
                    line.add(new StackedItemId(itemId, item.getAmount()));
                }
            }
            if (!ListUtils.isAllNull(line)) {
                ingredients.add(line);
            }
        }
        return ListUtils.removeEmptyColumnAndLine(ingredients);
    }

    public boolean isEmpty() {
        return inputs.isEmpty();
    }

    public Integer inputCount() {
        return inputCount;
    }

    public Integer width() {
        return width;
    }

    public Integer height() {
        return height;
    }

    public StackedItemId getInputItem(int line, int row) {
        return inputs.get(line).get(row);
    }

}
