package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.RecipeSmoking;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftSmokingRecipe;
import org.bukkit.craftbukkit.v1_20_R4.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class SmokingRecipe12005 extends RecipeSmoking {

    private final RecipeChoice ingredient;

    SmokingRecipe12005(String group, CookingBookCategory cookingbookcategory, RecipeItemStack nmsIngredient, RecipeChoice ingredient, ItemStack result, float exp, int smeltTick) {
        super(group, cookingbookcategory, nmsIngredient, result, exp, smeltTick);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(IInventory input, World var1) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.a(0)));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.g());
        CraftSmokingRecipe recipe = new CraftSmokingRecipe(recipeKey, result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.f()));
        return recipe;
    }

    public static RecipeHolder<RecipeSmoking> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmokingRecipe bukkitRecipe) {
        CraftSmokingRecipe craftSmokingRecipe = CraftSmokingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(CraftNamespacedKey.toMinecraft(recipeKey), new SmokingRecipe12005(craftSmokingRecipe.getGroup(), CraftRecipe.getCategory(craftSmokingRecipe.getCategory()), craftSmokingRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true), recipeChoice, CraftItemStack.asNMSCopy(bukkitRecipe.getResult()), bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}
