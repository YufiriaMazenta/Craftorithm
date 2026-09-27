package pers.yufiria.craftorithm.recipe.nms.paper;

import com.google.common.collect.Maps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.CustomShapedRecipePattern;
import pers.yufiria.craftorithm.recipe.nms.common.NmsShapedRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ShapedRecipe12110 extends ShapedRecipe implements NmsShapedRecipe<CraftingInput> {

    private final NamespacedKey recipeKey;
    private final CustomShapedRecipePattern customPattern;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapedRecipe12110(
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

    @Override
    public BukkitCraftingInput toBukkitCraftingInput(CraftingInput craftingInput) {
        int width = craftingInput.width();
        int height = craftingInput.height();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<>(width * height);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                items.add(CraftItemStack.asCraftMirror(craftingInput.getItem(column, row)));
            }
        }
        return new BukkitCraftingInput(items, width, height);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return matchesShapedSince1_21(input, input.ingredientCount(), level.getWorld());
    }

    public static RecipeHolder<ShapedRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        Map<Character, RecipeChoice> bukkitIngredients = craftRecipe.getChoiceMap();
        String[] shape = CustomShapedRecipePattern.replaceUndefinedIngredients(craftRecipe.getShape(), bukkitIngredients);
        bukkitIngredients.values().removeIf(Objects::isNull);
        Map<Character, Ingredient> nmsIngredients = Maps.transformValues(bukkitIngredients, (recipeChoice) -> craftRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), false));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(nmsIngredients, shape);
        CustomShapedRecipePattern customPattern = CustomShapedRecipePattern.fromBukkitRecipe(shapedRecipe);
        ShapedRecipe nmsRecipe = new ShapedRecipe12110(recipeKey, craftRecipe.getGroup(), CraftRecipe.getCategory(craftRecipe.getCategory()), pattern, CraftItemStack.asNMSCopy(shapedRecipe.getResult()), customPattern);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsRecipe);
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        return (org.bukkit.inventory.ShapedRecipe) cachedBukkitRecipe.get(() -> {
            CraftShapedRecipe recipe;
            org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
            recipe = new CraftShapedRecipe(id, result, this);
            recipe.setGroup(this.group());
            recipe.setCategory(CraftRecipe.getCategory(this.category()));
            String[] shape = this.customPattern.shapeArray();
            if (shape != null) {
                recipe.shape(shape);
            }
            this.customPattern.applyIngredients(recipe::setIngredient);
            return recipe;
        });
    }
}
