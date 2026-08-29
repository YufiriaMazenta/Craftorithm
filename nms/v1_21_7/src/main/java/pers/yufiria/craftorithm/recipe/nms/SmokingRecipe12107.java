package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftSmokingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class SmokingRecipe12107 extends RecipeSmoking {

    private final RecipeChoice ingredient;
    private volatile Recipe cachedBukkitRecipe;

    SmokingRecipe12107(
        String group,
        CookingBookCategory cookingbookcategory,
        RecipeItemStack recipeitemstack,
        RecipeChoice ingredient,
        ItemStack result,
        float exp,
        int smeltTick
    ) {
        super(group, cookingbookcategory, recipeitemstack, result, exp, smeltTick);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(SingleRecipeInput input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.c()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.l());
        CraftSmokingRecipe recipe = new CraftSmokingRecipe(id, result, ingredient, this.c(), this.d());
        recipe.setGroup(this.j());
        recipe.setCategory(CraftRecipe.getCategory(this.e()));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<RecipeSmoking> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmokingRecipe bukkitRecipe) {
        CraftSmokingRecipe craftFurnaceRecipe = CraftSmokingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(
            CraftRecipe.toMinecraft(recipeKey),
            new SmokingRecipe12107(
                craftFurnaceRecipe.getGroup(),
                CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()),
                craftFurnaceRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true),
                recipeChoice,
                CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
                bukkitRecipe.getExperience(),
                bukkitRecipe.getCookingTime()
            )
        );
    }

}
