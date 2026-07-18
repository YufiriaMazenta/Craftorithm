package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class StonecutterRecipe12110 extends StonecutterRecipe {

    private final RecipeChoice ingredient;

    StonecutterRecipe12110(String group, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStack result) {
        super(group, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.item()));
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result());
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, ingredient);
        recipe.setGroup(this.group());
        return recipe;
    }

    public static RecipeHolder<StonecutterRecipe> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new StonecutterRecipe12110(craftRecipe.getGroup(), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getInputChoice()), true), craftRecipe.getInputChoice(), CraftItemStack.asNMSCopy(craftRecipe.getResult())));
    }
}
