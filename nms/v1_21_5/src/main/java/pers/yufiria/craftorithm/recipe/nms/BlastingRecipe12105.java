package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftBlastingRecipe;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class BlastingRecipe12105 extends RecipeBlasting {

    private final RecipeChoice ingredient;

    BlastingRecipe12105(String group, CookingBookCategory cookingbookcategory, RecipeItemStack recipeitemstack, RecipeChoice ingredient, ItemStack result, float exp, int smeltTick) {
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
        CraftBlastingRecipe recipe = new CraftBlastingRecipe(id, result, ingredient, this.c(), this.d());
        recipe.setGroup(this.j());
        recipe.setCategory(CraftRecipe.getCategory(this.e()));
        return recipe;
    }

    public static RecipeHolder<RecipeBlasting> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.BlastingRecipe bukkitRecipe) {
        CraftBlastingRecipe craftBlastingRecipe = CraftBlastingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new BlastingRecipe12105(craftBlastingRecipe.getGroup(), CraftRecipe.getCategory(craftBlastingRecipe.getCategory()), craftBlastingRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true), recipeChoice, CraftItemStack.asNMSCopy(bukkitRecipe.getResult()), bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}

