package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class FurnaceRecipe12100 extends FurnaceRecipe {

    private final RecipeChoice ingredient;

    public FurnaceRecipe12100(String group, CookingBookCategory cookingbookcategory, RecipeItemStack recipeitemstack, RecipeChoice ingredient, ItemStack result, float exp, int smeltTick) {
        super(group, cookingbookcategory, recipeitemstack, result, exp, smeltTick);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(SingleRecipeInput input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.c()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.g());
        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(id, result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.f()));
        return recipe;
    }

    public static RecipeHolder<FurnaceRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.FurnaceRecipe bukkitRecipe) {
        CraftFurnaceRecipe craftFurnaceRecipe = CraftFurnaceRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new FurnaceRecipe12100(
                craftFurnaceRecipe.getGroup(),
                CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()),
                craftFurnaceRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true),
                recipeChoice,
                CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
                bukkitRecipe.getExperience(),
                bukkitRecipe.getCookingTime()
            )
        );
    }
}
