package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.ShapedRecipes;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.CustomShapedRecipePattern;
import pers.yufiria.craftorithm.recipe.nms.common.NmsShapedRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ShapedRecipe12002 extends ShapedRecipes implements NmsShapedRecipe<InventoryCrafting> {

    private final NamespacedKey recipeKey;
    private final CustomShapedRecipePattern customPattern;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapedRecipe12002(
        NamespacedKey recipeKey,
        String group,
        CraftingBookCategory craftingbookcategory,
        int width,
        int height,
        NonNullList<RecipeItemStack> nmsIngredients,
        ItemStack result,
        CustomShapedRecipePattern customPattern
    ) {
        super(group, craftingbookcategory, width, height, nmsIngredients, result);
        this.recipeKey = recipeKey;
        this.customPattern = customPattern;
        this.result = result;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public CustomShapedRecipePattern customPattern() {
        return customPattern;
    }

    /**
     * 对应match方法
     */
    @Override
    public boolean a(InventoryCrafting inventorycrafting, World world) {
        return matchesShapedBefore1_21(inventorycrafting, world.getWorld());
    }

    @Override
    public BukkitCraftingInput toBukkitCraftingInput(InventoryCrafting craftingInput) {
        int width = craftingInput.f();
        int height = craftingInput.g();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<>(width * height);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                items.add(CraftItemStack.asCraftMirror(craftingInput.a(column + row * width)));
            }
        }
        return new BukkitCraftingInput(items, width, height);
    }

    public static RecipeHolder<ShapedRecipes> fromBukkit(NamespacedKey recipeKey, ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        Map<Character, RecipeChoice> bukkitIngredients = craftRecipe.getChoiceMap();
        String[] shape = CustomShapedRecipePattern.replaceUndefinedIngredients(craftRecipe.getShape(), bukkitIngredients);
        bukkitIngredients.values().removeIf(Objects::isNull);
        CustomShapedRecipePattern customPattern = CustomShapedRecipePattern.fromBukkitRecipe(shapedRecipe);

        NonNullList<RecipeItemStack> nmsIngredients = NonNullList.a(shape.length * customPattern.width(), RecipeItemStack.a);

        for(int i = 0; i < shape.length; ++i) {
            String row = shape[i];

            for(int j = 0; j < row.length(); ++j) {
                nmsIngredients.set(
                    i * customPattern.width() + j,
                    craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitIngredients.get(row.charAt(j))), false)
                );
            }
        }

        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new ShapedRecipe12002(
                recipeKey,
                craftRecipe.getGroup(),
                CraftRecipe.getCategory(craftRecipe.getCategory()),
                customPattern.width(),
                customPattern.height(),
                nmsIngredients,
                CraftItemStack.asNMSCopy(shapedRecipe.getResult()),
                customPattern
            )
        );
    }

    @Override
    public ShapedRecipe toBukkitRecipe(NamespacedKey recipeKey) {
        return (ShapedRecipe) cachedBukkitRecipe.get(() -> {
            CraftShapedRecipe recipe;
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            recipe = new CraftShapedRecipe(recipeKey, result, this);
            recipe.setGroup(this.c());
            recipe.setCategory(CraftRecipe.getCategory(this.d()));

            String[] shape = this.customPattern.shapeArray();
            if (shape != null) {
                recipe.shape(shape);
            }
            this.customPattern.applyIngredients(recipe::setIngredient);

            return recipe;
        });
    }
}
