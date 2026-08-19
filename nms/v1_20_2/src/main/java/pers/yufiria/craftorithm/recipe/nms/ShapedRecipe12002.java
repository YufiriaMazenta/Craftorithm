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
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ShapedRecipe12002 extends ShapedRecipes {

    private final ShapedRecipePattern12002 customPattern;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    ShapedRecipe12002(
        String group,
        CraftingBookCategory craftingbookcategory,
        int width,
        int height,
        NonNullList<RecipeItemStack> nmsIngredients,
        ItemStack result,
        ShapedRecipePattern12002 customPattern
    ) {
        super(group, craftingbookcategory, width, height, nmsIngredients, result);
        this.customPattern = customPattern;
        this.result = result;
    }

    /**
     * 对应match方法
     */
    @Override
    public boolean a(InventoryCrafting inventorycrafting, World world) {
        return this.customPattern.matches(inventorycrafting);
    }

    public static RecipeHolder<ShapedRecipes> fromBukkit(NamespacedKey recipeKey, ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        Map<Character, RecipeChoice> bukkitIngredients = craftRecipe.getChoiceMap();
        String[] shape = replaceUndefinedIngredientsWithEmpty(craftRecipe.getShape(), bukkitIngredients);
        bukkitIngredients.values().removeIf(Objects::isNull);
        ShapedRecipePattern12002 customPattern = ShapedRecipePattern12002.fromBukkitRecipe(shapedRecipe);

        NonNullList<RecipeItemStack> nmsIngredients = NonNullList.a(shape.length * customPattern.width, RecipeItemStack.a);

        for(int i = 0; i < shape.length; ++i) {
            String row = shape[i];

            for(int j = 0; j < row.length(); ++j) {
                nmsIngredients.set(
                    i * customPattern.width + j,
                    craftRecipe.toNMS(RecipeUtils.getBukkitChoice(bukkitIngredients.get(row.charAt(j))), false)
                );
            }
        }

        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new ShapedRecipe12002(
                craftRecipe.getGroup(),
                CraftRecipe.getCategory(craftRecipe.getCategory()),
                customPattern.width,
                customPattern.height,
                nmsIngredients,
                CraftItemStack.asNMSCopy(shapedRecipe.getResult()),
                customPattern
            )
        );
    }

    private static String[] replaceUndefinedIngredientsWithEmpty(String[] shape, Map<Character, RecipeChoice> ingredients) {
        for(int i = 0; i < shape.length; ++i) {
            String row = shape[i];
            StringBuilder filteredRow = new StringBuilder(row.length());

            for(char character : row.toCharArray()) {
                filteredRow.append(ingredients.get(character) == null ? ' ' : character);
            }

            shape[i] = filteredRow.toString();
        }

        return shape;
    }

    @Override
    public ShapedRecipe toBukkitRecipe(NamespacedKey recipeKey) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return (ShapedRecipe) cached;
        }
        CraftShapedRecipe recipe;
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        recipe = new CraftShapedRecipe(recipeKey, result, this);
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.d()));
        switch (this.customPattern.height()) {
            case 1:
                switch (this.customPattern.width()) {
                    case 1 -> recipe.shape("a");
                    case 2 -> recipe.shape("ab");
                    case 3 -> recipe.shape("abc");
                    default -> { }
                }
            case 2:
                switch (this.customPattern.width()) {
                    case 1 -> recipe.shape("a", "b");
                    case 2 -> recipe.shape("ab", "cd");
                    case 3 -> recipe.shape("abc", "def");
                    default -> { }
                }
            case 3:
                switch (this.customPattern.width()) {
                    case 1 -> recipe.shape("a", "b", "c");
                    case 2 -> recipe.shape("ab", "cd", "ef");
                    case 3 -> recipe.shape("abc", "def", "ghi");
                }
        }

        char c = 'a';

        for (Optional<RecipeChoice> ingredient : this.customPattern.ingredients()) {
            if (ingredient.isPresent()) {
                recipe.setIngredient(c, ingredient.get());
            }
            ++ c;
        }

        cachedBukkitRecipe = recipe;
        return recipe;
    }
}
