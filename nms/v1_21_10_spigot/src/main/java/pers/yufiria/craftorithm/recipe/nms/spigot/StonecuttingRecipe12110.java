package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class StonecuttingRecipe12110 extends RecipeStonecutting {

    private final RecipeChoice ingredient;

    StonecuttingRecipe12110(String group, RecipeItemStack nmsIngredient, RecipeChoice ingredient, ItemStack result) {
        super(group, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(SingleRecipeInput input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.c()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.l());
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, ingredient);
        recipe.setGroup(this.j());
        return recipe;
    }

    public static RecipeHolder<RecipeStonecutting> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new RecipeStonecutting(craftRecipe.getGroup(), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getInputChoice()), true), CraftItemStack.asNMSCopy(craftRecipe.getResult())));
    }
}


