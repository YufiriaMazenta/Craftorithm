package pers.yufiria.craftorithm.recipe.nms.spigot;

import com.google.common.collect.Maps;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ShapedRecipe260100 extends ShapedRecipe {

    private final ShapedRecipePattern260100 customPattern;
    private final ItemStackTemplate result;
    private volatile org.bukkit.inventory.Recipe cachedBukkitRecipe;

    ShapedRecipe260100(
        Recipe.CommonInfo commonInfo,
        CraftingRecipe.CraftingBookInfo bookInfo,
        ShapedRecipePattern pattern,
        ItemStackTemplate result,
        ShapedRecipePattern260100 customPattern
    ) {
        super(commonInfo, bookInfo, pattern, result);
        this.customPattern = customPattern;
        this.result = result;
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
        Map<Character, Ingredient> nmsIngredients = Maps.transformValues(bukkitIngredients, (recipeChoice) -> craftRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), false));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(nmsIngredients, shape);
        ShapedRecipePattern260100 customPattern = ShapedRecipePattern260100.fromBukkitRecipe(shapedRecipe);

        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(shapedRecipe.getResult()));
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        CraftingRecipe.CraftingBookInfo bookInfo = new CraftingRecipe.CraftingBookInfo(CraftRecipe.getCategory(craftRecipe.getCategory()), craftRecipe.getGroup());
        ShapedRecipe nmsRecipe = new ShapedRecipe260100(commonInfo, bookInfo, pattern, resultTemplate, customPattern);
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
        ItemStack result = CraftItemStack.asCraftMirror(this.result);
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
