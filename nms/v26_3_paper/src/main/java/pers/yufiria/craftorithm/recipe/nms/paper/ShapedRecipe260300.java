package pers.yufiria.craftorithm.recipe.nms.paper;

import com.google.common.collect.Maps;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.CustomShapedRecipePattern;
import pers.yufiria.craftorithm.recipe.nms.NmsShapedRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ShapedRecipe260300 extends ShapedRecipe implements NmsShapedRecipe<CraftingInput> {

    private final NamespacedKey recipeKey;
    private final CustomShapedRecipePattern customPattern;
    private final ItemStackTemplate result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapedRecipe260300(
        NamespacedKey recipeKey,
        Recipe.CommonInfo commonInfo,
        CraftingRecipe.CraftingBookInfo bookInfo,
        ShapedRecipePattern pattern,
        ItemStackTemplate result,
        CustomShapedRecipePattern customPattern
    ) {
        super(commonInfo, bookInfo, pattern, result);
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

    @Override
    public BukkitCraftingInput toBukkitCraftingInput(CraftingInput craftingInput) {
        int width = craftingInput.width();
        int height = craftingInput.height();
        List<ItemStack> items = new ArrayList<>(width * height);
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                items.add(CraftItemStack.asBukkitMirror(craftingInput.getItem(column, row)));
            }
        }
        return new BukkitCraftingInput(items, width, height, craftingInput.ingredientCount());
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return matches(toBukkitCraftingInput(input), level.getWorld());
    }

    public static RecipeHolder<ShapedRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        Map<Character, RecipeChoice> bukkitIngredients = craftRecipe.getChoiceMap();
        String[] shape = CustomShapedRecipePattern.replaceUndefinedIngredients(craftRecipe.getShape(), bukkitIngredients);
        bukkitIngredients.values().removeIf(Objects::isNull);
        Map<Character, Ingredient> nmsIngredients = Maps.transformValues(bukkitIngredients, (recipeChoice) -> CraftRecipe.toIngredient(IngredientUtils.getBukkitChoice(recipeChoice), false));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(nmsIngredients, shape);
        CustomShapedRecipePattern customPattern = CustomShapedRecipePattern.fromBukkitRecipe(shapedRecipe);

        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(shapedRecipe.getResult()));
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        CraftingRecipe.CraftingBookInfo bookInfo = new CraftingRecipe.CraftingBookInfo(CraftRecipe.getCategory(craftRecipe.getCategory()), craftRecipe.getGroup());
        ShapedRecipe nmsRecipe = new ShapedRecipe260300(recipeKey, commonInfo, bookInfo, pattern, resultTemplate, customPattern);
        return new RecipeHolder<>(CraftNamespacedKey.toResourceKey(Registries.RECIPE, recipeKey), nmsRecipe);
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        return (org.bukkit.inventory.ShapedRecipe) cachedBukkitRecipe.get(() -> {
            CraftShapedRecipe recipe;
            ItemStack result = CraftItemStack.asBukkitMirror(this.result.create());
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
