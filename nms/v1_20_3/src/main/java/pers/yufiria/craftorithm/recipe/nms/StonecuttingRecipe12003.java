package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class StonecuttingRecipe12003 extends RecipeStonecutting implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    StonecuttingRecipe12003(
        NamespacedKey recipeKey,
        String group,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result
    ) {
        super(group, nmsIngredient, result);
        this.recipeKey = recipeKey;
        this.ingredient = ingredient;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public RecipeChoice ingredientChoice() {
        return ingredient;
    }

    @Override
    public boolean a(IInventory input, World world) {
        return matches(CraftItemStack.asCraftMirror(input.a(0)), world.getWorld());
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        return cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.b);
            CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(recipeKey, result, ingredient);
            recipe.setGroup(this.c());
            return recipe;
        });
    }

    public static RecipeHolder<RecipeStonecutting> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new StonecuttingRecipe12003(
                recipeKey,
                craftRecipe.getGroup(),
                craftRecipe.toNMS(
                    IngredientUtils.getBukkitChoice(craftRecipe.getInputChoice()), true
                ),
                craftRecipe.getInputChoice(),
                CraftItemStack.asNMSCopy(craftRecipe.getResult())
            )
        );
    }

}
