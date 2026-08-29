package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class StonecuttingRecipe12000 extends RecipeStonecutting {
    private final RecipeChoice ingredient;
    private volatile Recipe cachedBukkitRecipe;

    StonecuttingRecipe12000(
        MinecraftKey recipeKey,
        String group,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result
    ) {
        super(recipeKey, group, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(IInventory input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.a(0)));
    }

    @Override
    public Recipe toBukkitRecipe() {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        NamespacedKey recipeKey = CraftNamespacedKey.fromMinecraft(this.c);
        CraftItemStack result = CraftItemStack.asCraftMirror(this.b);
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(recipeKey, result, ingredient);
        recipe.setGroup(this.c());
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeStonecutting fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        return new StonecuttingRecipe12000(
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftRecipe.getGroup(),
            craftRecipe.toNMS(
                IngredientUtils.getBukkitChoice(craftRecipe.getInputChoice()), true
            ),
            craftRecipe.getInputChoice(),
            CraftItemStack.asNMSCopy(craftRecipe.getResult())
        );
    }

}
