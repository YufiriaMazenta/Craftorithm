package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.util.IOHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.StonecuttingRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class StonecuttingRecipe12107 extends RecipeStonecutting {

    private final RecipeChoice ingredient;

    public StonecuttingRecipe12107(
        String group,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result
    ) {
        super(group, nmsIngredient, result);
        this.ingredient = ingredient;
    }

    @Override
    public boolean a(SingleRecipeInput input, World world) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.c()));
    }



    @Override
    public ItemStack a(SingleRecipeInput input, HolderLookup.a var1) {
        if (ingredient.test(CraftItemStack.asCraftMirror(input.c()))) {
            return super.a(input, var1);
        }
        return ItemStack.l;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.l());
        CraftStonecuttingRecipe recipe = new CraftStonecuttingRecipe(id, result, ingredient);
        recipe.setGroup(this.j());
        return recipe;
    }

    public static RecipeHolder<RecipeStonecutting> fromBukkit(NamespacedKey recipeKey, StonecuttingRecipe bukkitRecipe) {
        CraftStonecuttingRecipe craftRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice inputChoice = craftRecipe.getInputChoice();
        RecipeChoice bukkitChoice = RecipeUtils.getBukkitChoice(inputChoice);
        if (bukkitChoice instanceof RecipeChoice.ExactChoice exactChoice) {
            List<Material> choices = new ArrayList<>();
            for (org.bukkit.inventory.ItemStack itemStack : exactChoice.getChoices()) {
                choices.add(itemStack.getType());
            }
            bukkitChoice = new RecipeChoice.MaterialChoice(choices);
        }
        return new RecipeHolder<>(
            CraftRecipe.toMinecraft(recipeKey),
            new StonecuttingRecipe12107(craftRecipe.getGroup(),
                craftRecipe.toNMS(bukkitChoice, true),
                inputChoice,
                CraftItemStack.asNMSCopy(craftRecipe.getResult())
            )
        );
    }

}
