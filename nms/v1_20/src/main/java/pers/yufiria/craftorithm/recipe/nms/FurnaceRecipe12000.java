package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.FurnaceRecipe;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class FurnaceRecipe12000 extends FurnaceRecipe {

    private final RecipeChoice ingredient;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    FurnaceRecipe12000(
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
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(CraftNamespacedKey.fromMinecraft(this.e()), result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.g()));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static FurnaceRecipe fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.FurnaceRecipe bukkitRecipe) {
        CraftFurnaceRecipe craftFurnaceRecipe = CraftFurnaceRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new FurnaceRecipe12000(
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
