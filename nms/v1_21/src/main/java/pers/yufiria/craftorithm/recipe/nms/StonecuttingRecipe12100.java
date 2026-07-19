package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class StonecuttingRecipe12100 extends RecipeStonecutting {

    private final RecipeChoice ingredient;

    StonecuttingRecipe12100(String group, RecipeItemStack nmsIngredient, RecipeChoice ingredient, ItemStack result) {
        super(group, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(SingleRecipeInput input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.c()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.g());
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, ingredient);
        recipe.setGroup(this.c());
        return recipe;
    }

    public static RecipeHolder<RecipeStonecutting> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftNamespacedKey.toMinecraft(recipeKey), new RecipeStonecutting(craftRecipe.getGroup(), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getInputChoice()), true), CraftItemStack.asNMSCopy(craftRecipe.getResult())));
    }
}
