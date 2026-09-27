package pers.yufiria.craftorithm.recipe.nms;

import com.google.common.collect.Maps;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
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

public final class ShapedRecipe12003 extends ShapedRecipes implements NmsShapedRecipe<InventoryCrafting> {

    private final NamespacedKey recipeKey;
    private final CustomShapedRecipePattern customPattern;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapedRecipe12003(
        NamespacedKey recipeKey,
        String group,
        CraftingBookCategory category,
        ShapedRecipePattern pattern,
        ItemStack result,
        CustomShapedRecipePattern customPattern
    ) {
        super(group, category, pattern, result);
        this.recipeKey = recipeKey;
        this.result = result;
        this.customPattern = customPattern;
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

        Map<Character, RecipeItemStack> nmsIngredients = Maps.transformValues(
            bukkitIngredients,
            (recipeChoice) ->
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), false)
        );
        ShapedRecipePattern pattern = ShapedRecipePattern.a(nmsIngredients, shape);

        ShapedRecipes nmsShapedRecipe = new ShapedRecipe12003(
            recipeKey,
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            pattern,
            CraftItemStack.asNMSCopy(shapedRecipe.getResult()),
            customPattern
        );
        return new RecipeHolder<>(CraftNamespacedKey.toMinecraft(recipeKey), nmsShapedRecipe);
    }

    @Override
    public ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        return (ShapedRecipe) cachedBukkitRecipe.get(() -> {
            CraftShapedRecipe recipe;
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            recipe = new CraftShapedRecipe(id, result, this);
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
