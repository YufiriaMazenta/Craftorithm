package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeBlasting;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.RecipeSmoking;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.*;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class SmokingRecipe12000 extends RecipeSmoking {

    private final RecipeChoice ingredient;
    private final ItemStack result;

    SmokingRecipe12000(
        MinecraftKey recipeKey,
        String group,
        CookingBookCategory cookingbookcategory,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result,
        float exp,
        int smeltTick
    ) {
        super(recipeKey, group, cookingbookcategory, nmsIngredient, result, exp, smeltTick);
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean a(IInventory input, World var1) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.a(0)));
    }

    @Override
    public Recipe toBukkitRecipe() {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftSmokingRecipe recipe = new CraftSmokingRecipe(CraftNamespacedKey.fromMinecraft(this.e()), result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.g()));
        return recipe;
    }

    public static RecipeSmoking fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmokingRecipe bukkitRecipe) {
        CraftSmokingRecipe craftFurnaceRecipe = CraftSmokingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new SmokingRecipe12000(
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftFurnaceRecipe.getGroup(),
            CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()),
            craftFurnaceRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true),
            recipeChoice,
            CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
            bukkitRecipe.getExperience(),
            bukkitRecipe.getCookingTime()
        );
    }

}
