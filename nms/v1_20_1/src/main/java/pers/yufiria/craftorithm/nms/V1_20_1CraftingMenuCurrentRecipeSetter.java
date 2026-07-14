package pers.yufiria.craftorithm.nms;

import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.ShapedRecipes;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;

@LifeCycleTaskSettings(
    rules = {@TaskRule(lifeCycle = LifeCycle.LOAD)}
)
public enum V1_20_1CraftingMenuCurrentRecipeSetter implements CraftingMenuCurrentRecipeSetter {

    INSTANCE;

    @Override
    public boolean setCurrentRecipe(CraftingInventory craftingInventory, NamespacedKey recipeKey, Recipe recipe) {
        if (!(recipe instanceof ShapedRecipe shapedRecipe)) {
            return false;
        }
        CraftShapedRecipe craftShapedRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        String[] shape = craftShapedRecipe.getShape();
        Map<Character, RecipeChoice> ingredients = craftShapedRecipe.getChoiceMap();
        int width = shape[0].length();
        NonNullList<RecipeItemStack> data = NonNullList.a(shape.length * width, RecipeItemStack.a);

        for(int i = 0; i < shape.length; ++i) {
            String row = shape[i];

            for(int j = 0; j < row.length(); ++j) {
                data.set(i * width + j, craftShapedRecipe.toNMS(ingredients.get(row.charAt(j)), false));
            }
        }

        ShapedRecipes nmsShapedRecipe = new ShapedRecipes(
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftShapedRecipe.getGroup(),
            CraftRecipe.getCategory(
                craftShapedRecipe.getCategory()
            ),
            width,
            shape.length,
            data,
            CraftItemStack.asNMSCopy(craftingInventory.getResult())
        );

        ((CraftInventoryCrafting) craftingInventory).getMatrixInventory().setCurrentRecipe(nmsShapedRecipe);
        return false;
    }
}
