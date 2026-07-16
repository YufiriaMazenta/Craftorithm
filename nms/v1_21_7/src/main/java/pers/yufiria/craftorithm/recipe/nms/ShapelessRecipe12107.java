package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.recipe.choice.CustomRecipeChoice;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12107 extends ShapelessRecipes {

    private final List<RecipeChoice> customIngredients;

    public ShapelessRecipe12107(
        String group,
        CraftingBookCategory category,
        ItemStack result,
        List<RecipeItemStack> nmsIngredients,
        List<RecipeChoice> customIngredients
    ) {
        super(group, category, result, nmsIngredients);
        this.customIngredients = customIngredients;
    }

    @Override
    public boolean a(CraftingInput craftinginput, World world) {
        if (craftinginput.e() != this.customIngredients.size()) {
            return false;
        }
        if (craftinginput.a() == 1 && customIngredients.size() == 1) {
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asBukkitCopy(craftinginput.a(0));
            return customIngredients.getFirst().test(bukkitCopy);
        }
        return craftinginput.c().a(this, null);
    }

    public static RecipeHolder<ShapelessRecipes> fromBukkit(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<RecipeItemStack> nmsIngredients = new ArrayList<>(bukkitIngredients.size());

        for(RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(
                recipeChoice instanceof CustomRecipeChoice ?
                craftRecipe.toNMS(((CustomRecipeChoice) recipeChoice).bukkitChoice(), true)
                :craftRecipe.toNMS(recipeChoice, true)
            );
        }

        ShapelessRecipes nmsRecipe = new ShapelessRecipe12107(
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            CraftItemStack.asNMSCopy(craftRecipe.getResult()),
            nmsIngredients,
            bukkitIngredients
        );

        return new RecipeHolder<>(
            CraftRecipe.toMinecraft(recipeKey),
            nmsRecipe
        );
    }

}
