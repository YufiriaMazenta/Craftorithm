package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeCampfire;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftCampfireRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class CampfireRecipe12000 extends RecipeCampfire {

    private final RecipeChoice ingredient;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    CampfireRecipe12000(
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
        CraftCampfireRecipe recipe = new CraftCampfireRecipe(CraftNamespacedKey.fromMinecraft(this.e()), result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.g()));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeCampfire fromBukkit(NamespacedKey recipeKey, CampfireRecipe bukkitRecipe) {
        CraftCampfireRecipe craftFurnaceRecipe = CraftCampfireRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new CampfireRecipe12000(
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftFurnaceRecipe.getGroup(),
            CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()),
            craftFurnaceRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true),
            recipeChoice,
            CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
            bukkitRecipe.getExperience(),
            bukkitRecipe.getCookingTime()
        );
    }

}
