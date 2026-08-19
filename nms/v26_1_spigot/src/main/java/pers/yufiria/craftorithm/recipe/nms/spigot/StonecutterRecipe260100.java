package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class StonecutterRecipe260100 extends StonecutterRecipe {

    private final RecipeChoice ingredient;
    private volatile org.bukkit.inventory.Recipe cachedBukkitRecipe;

    StonecutterRecipe260100(Recipe.CommonInfo commonInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result) {
        super(commonInfo, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.item()));
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result());
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, ingredient);
        recipe.setGroup(this.group());
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<StonecutterRecipe> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(craftRecipe.getResult()));
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new StonecutterRecipe260100(commonInfo, craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getInputChoice()), true), craftRecipe.getInputChoice(), resultTemplate));
    }
}
