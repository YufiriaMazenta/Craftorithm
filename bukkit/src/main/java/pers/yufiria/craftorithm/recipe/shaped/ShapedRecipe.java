package pers.yufiria.craftorithm.recipe.shaped;

import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemId;
import pers.yufiria.craftorithm.recipe.Recipe;
import pers.yufiria.craftorithm.recipe.RecipeIngredient;
import pers.yufiria.craftorithm.recipe.RecipeResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShapedRecipe implements Recipe {

    private final @NotNull NamespacedKey key;
    private @NotNull RecipeResult result;
    private List<List<RecipeIngredient>> ingredients;

    public ShapedRecipe(@NotNull NamespacedKey key, @NotNull RecipeResult result, List<List<RecipeIngredient>> ingredients) {
        this.key = key;
        this.result = result;
        this.ingredients = ingredients;
    }

    public int match(ItemStack[] items) {
        List<List<RecipeIngredient>> ingredients = toIngredientsShape(items);
        //TODO 比对
        return 0;
    }

    @Override
    public @NotNull NamespacedKey recipeKey() {
        return key;
    }

    @Override
    public @NotNull RecipeResult result() {
        return result;
    }

    public static List<List<RecipeIngredient>> toIngredientsShape(@NotNull ItemStack[] items) {
        Objects.requireNonNull(items, "items cannot be null");
        List<List<RecipeIngredient>> ingredients = new ArrayList<>();
        if (items.length == 4) {
            //是玩家背包页面的合成
        } else {
            //是工作台页面的合成
            for (int i = 0; i < 3; i++) {
                List<RecipeIngredient> line = new ArrayList<>();
                for (int j = 0; j < 3; j++) {
                    int index = i * 3 + j;
                    ItemStack item = items[index];
                    if (ItemHelper.isAir(item)) {
                        line.add(null);
                    } else {
                        NamespacedItemId itemId = ItemManager.INSTANCE.matchItemId(item);
                        if (itemId == null) {
                            itemId = new NamespacedItemId(item.getType().getKey().getNamespace(), item.getType().getKey().getKey());
                        }
                        line.add(new RecipeIngredient(itemId, item.getAmount()));
                    }
                }
                ingredients.add(line);
            }
        }
        return removeEmptyColumnAndLine(ingredients);
    }

    private static List<List<RecipeIngredient>> removeEmptyColumnAndLine(@NotNull List<List<RecipeIngredient>> ingredients) {
        //TODO

    }

}
