package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class FurnaceRecipe12110 extends FurnaceRecipe {

    private final RecipeChoice ingredient;

    public FurnaceRecipe12110(String group, CookingBookCategory cookingbookcategory, RecipeItemStack recipeitemstack, RecipeChoice ingredient, ItemStack result, float exp, int smeltTick) {
        super(group, cookingbookcategory, recipeitemstack, result, exp, smeltTick);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(SingleRecipeInput input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.c()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.l());
        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(id, result, ingredient, this.c(), this.d());
        recipe.setGroup(this.j());
        recipe.setCategory(CraftRecipe.getCategory(this.e()));
        return recipe;
    }

    public static RecipeHolder<FurnaceRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.FurnaceRecipe bukkitRecipe) {
        CraftFurnaceRecipe craftFurnaceRecipe = CraftFurnaceRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new FurnaceRecipe12110(craftFurnaceRecipe.getGroup(), CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()), craftFurnaceRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true), recipeChoice, CraftItemStack.asNMSCopy(bukkitRecipe.getResult()), bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}


