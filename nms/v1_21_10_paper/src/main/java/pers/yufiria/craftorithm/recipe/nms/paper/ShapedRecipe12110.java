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
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ShapedRecipe12110 extends ShapedRecipe {

    private final ShapedRecipePattern12110 customPattern;
    private final ItemStack result;
    private volatile org.bukkit.inventory.Recipe cachedBukkitRecipe;

    ShapedRecipe12110(
        String group,
        CraftingBookCategory category,
        ShapedRecipePattern pattern,
        ItemStack result,
        ShapedRecipePattern12110 customPattern
    ) {
        super(group, category, pattern, result);
        this.result = result;
        this.customPattern = customPattern;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return customPattern.matches(input);
    }

    public static RecipeHolder<ShapedRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        Map<Character, RecipeChoice> bukkitIngredients = craftRecipe.getChoiceMap();
        String[] shape = replaceUndefinedIngredientsWithEmpty(craftRecipe.getShape(), bukkitIngredients);
        bukkitIngredients.values().removeIf(Objects::isNull);
        Map<Character, Ingredient> nmsIngredients = Maps.transformValues(bukkitIngredients, (recipeChoice) -> craftRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), false));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(nmsIngredients, shape);
        ShapedRecipePattern12110 customPattern = ShapedRecipePattern12110.fromBukkitRecipe(shapedRecipe);
        ShapedRecipe nmsRecipe = new ShapedRecipe12110(craftRecipe.getGroup(), CraftRecipe.getCategory(craftRecipe.getCategory()), pattern, CraftItemStack.asNMSCopy(shapedRecipe.getResult()), customPattern);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsRecipe);
    }

    private static String[] replaceUndefinedIngredientsWithEmpty(String[] shape, Map<Character, RecipeChoice> ingredients) {
        for (int i = 0; i < shape.length; ++i) {
            String row = shape[i];
            StringBuilder filteredRow = new StringBuilder(row.length());
            for (char character : row.toCharArray()) {
                filteredRow.append(ingredients.get(character) == null ? ' ' : character);
            }
            shape[i] = filteredRow.toString();
        }
        return shape;
    }

    @Override
    public org.bukkit.inventory.ShapedRecipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return (org.bukkit.inventory.ShapedRecipe) cached;
        }
        CraftShapedRecipe recipe;
        org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
        recipe = new CraftShapedRecipe(id, result, this);
        recipe.setGroup(this.group());
        recipe.setCategory(CraftRecipe.getCategory(this.category()));
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
            char finalC = c;
            ingredient.ifPresent(recipeChoice -> recipe.setIngredient(finalC, recipeChoice));
            ++c;
        }
        cachedBukkitRecipe = recipe;
        return recipe;
    }
}
