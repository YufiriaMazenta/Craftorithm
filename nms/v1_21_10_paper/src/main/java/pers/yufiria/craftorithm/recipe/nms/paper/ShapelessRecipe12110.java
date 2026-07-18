package pers.yufiria.craftorithm.recipe.nms.paper;

import crypticlib.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12110 extends ShapelessRecipe {

    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;

    ShapelessRecipe12110(String group, CraftingBookCategory category, ItemStack result, List<Ingredient> nmsIngredients, List<RecipeChoice> customIngredients) {
        super(group, category, result, nmsIngredients);
        this.result = result;
        this.customIngredients = customIngredients;
    }

    @Override
    public boolean matches(CraftingInput craftinginput, Level level) {
        if (craftinginput.ingredientCount() != this.customIngredients.size()) return false;
        if (craftinginput.ingredientCount() == 1 && customIngredients.size() == 1) {
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(craftinginput.getItem(0));
            return customIngredients.getFirst().test(bukkitCopy);
        }
        List<org.bukkit.inventory.ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < craftinginput.ingredientCount(); i++) {
            ItemStack nmsStack = craftinginput.getItem(i);
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(nmsStack);
            if (!ItemHelper.isAir(bukkitCopy)) inputItems.add(bukkitCopy);
        }
        return RecipeUtils.matchItemsToChoices(inputItems, customIngredients);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
        recipe.setGroup(this.group());
        recipe.setCategory(CraftRecipe.getCategory(this.category()));
        for (RecipeChoice choice : this.customIngredients) {
            recipe.addIngredient(choice);
        }
        return recipe;
    }

    public static RecipeHolder<ShapelessRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<Ingredient> nmsIngredients = new ArrayList<>(bukkitIngredients.size());
        for (RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(craftRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true));
        }
        ShapelessRecipe nmsRecipe = new ShapelessRecipe12110(craftRecipe.getGroup(), CraftRecipe.getCategory(craftRecipe.getCategory()), CraftItemStack.asNMSCopy(craftRecipe.getResult()), nmsIngredients, bukkitIngredients);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsRecipe);
    }
}
