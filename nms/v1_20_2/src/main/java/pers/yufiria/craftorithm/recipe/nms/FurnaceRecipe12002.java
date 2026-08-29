package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.FurnaceRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class FurnaceRecipe12002 extends FurnaceRecipe {

    private final RecipeChoice ingredient;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    FurnaceRecipe12002(
        String group,
        CookingBookCategory cookingbookcategory,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result,
        float exp,
        int smeltTick
    ) {
        super(group, cookingbookcategory, nmsIngredient, result, exp, smeltTick);
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean a(IInventory input, World var1) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.a(0)));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(recipeKey, result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.b));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<FurnaceRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.FurnaceRecipe bukkitRecipe) {
        CraftFurnaceRecipe craftFurnaceRecipe = CraftFurnaceRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new FurnaceRecipe12002(
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
