package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeCampfire;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftCampfireRecipe;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class CampfireRecipe12003 extends RecipeCampfire {

    private final RecipeChoice ingredient;
    private volatile Recipe cachedBukkitRecipe;

    CampfireRecipe12003(
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
        CraftItemStack result = CraftItemStack.asCraftMirror(this.g());
        CraftCampfireRecipe recipe = new CraftCampfireRecipe(recipeKey, result, ingredient, this.b(), this.d());
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.f()));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<RecipeCampfire> fromBukkit(NamespacedKey recipeKey, CampfireRecipe bukkitRecipe) {
        CraftCampfireRecipe craftCampfireRecipe = CraftCampfireRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new CampfireRecipe12003(
                craftCampfireRecipe.getGroup(),
                CraftRecipe.getCategory(craftCampfireRecipe.getCategory()),
                craftCampfireRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true),
                recipeChoice,
                CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
                bukkitRecipe.getExperience(),
                bukkitRecipe.getCookingTime()
            )
        );
    }

}
