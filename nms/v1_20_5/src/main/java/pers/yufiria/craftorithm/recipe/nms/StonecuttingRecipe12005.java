package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_20_R4.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class StonecuttingRecipe12005 extends RecipeStonecutting {

    private final RecipeChoice ingredient;

    StonecuttingRecipe12005(String group, RecipeItemStack nmsIngredient, RecipeChoice ingredient, ItemStack result) {
        super(group, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(IInventory input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.a(0)));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.b);
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(recipeKey, result, ingredient);
        recipe.setGroup(this.c());
        return recipe;
    }

    public static RecipeHolder<RecipeStonecutting> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftNamespacedKey.toMinecraft(recipeKey), new StonecuttingRecipe12005(craftRecipe.getGroup(), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getInputChoice()), true), craftRecipe.getInputChoice(), CraftItemStack.asNMSCopy(craftRecipe.getResult())));
    }
}
