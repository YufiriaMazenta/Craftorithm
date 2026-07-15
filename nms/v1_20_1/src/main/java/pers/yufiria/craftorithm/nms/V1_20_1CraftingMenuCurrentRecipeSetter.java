package pers.yufiria.craftorithm.nms;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.ShapedRecipes;
import net.minecraft.world.item.crafting.ShapelessRecipes;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.recipe.finger.RecipeFingerManager;

import java.util.List;
import java.util.Map;

public enum V1_20_1CraftingMenuCurrentRecipeSetter implements CraftingMenuCurrentRecipeSetter {

    INSTANCE;

    @Override
    public boolean setCurrentRecipe(CraftingInventory craftingInventory, NamespacedKey recipeKey, Recipe recipe) {
        IRecipe<InventoryCrafting> nmsRecipe = switch (recipe) {
            case ShapedRecipe shapedRecipe -> createShapedRecipe(recipeKey, shapedRecipe);
            case ShapelessRecipe shapelessRecipe -> createShapelessRecipe(recipeKey, shapelessRecipe);
            default -> null;
        };
        if (nmsRecipe == null) {
            return false;
        }

        MinecraftKey nmsRecipeKey = CraftNamespacedKey.toMinecraft(recipeKey);
        var recipeManager = ((CraftServer) Bukkit.getServer()).getServer().aE();
        recipeManager.removeRecipe(nmsRecipeKey);
        recipeManager.addRecipe(nmsRecipe);
        ((CraftInventoryCrafting) craftingInventory).getMatrixInventory().setCurrentRecipe(nmsRecipe);
        return true;
    }

    private ShapedRecipes createShapedRecipe(NamespacedKey recipeKey, ShapedRecipe shapedRecipe) {
        CraftShapedRecipe craftRecipe = CraftShapedRecipe.fromBukkitRecipe(shapedRecipe);
        String[] shape = craftRecipe.getShape();
        Map<Character, RecipeChoice> ingredients = craftRecipe.getChoiceMap();
        int width = shape[0].length();
        NonNullList<RecipeItemStack> data = NonNullList.a(shape.length * width, RecipeItemStack.a);

        for (int rowIndex = 0; rowIndex < shape.length; rowIndex++) {
            String row = shape[rowIndex];
            for (int columnIndex = 0; columnIndex < row.length(); columnIndex++) {
                data.set(
                    rowIndex * width + columnIndex,
                    craftRecipe.toNMS(ingredients.get(row.charAt(columnIndex)), false)
                );
            }
        }

        return new FingerShapedRecipe(
            recipeKey,
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            width,
            shape.length,
            data,
            CraftItemStack.asNMSCopy(shapedRecipe.getResult())
        );
    }

    private ShapelessRecipes createShapelessRecipe(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> ingredients = craftRecipe.getChoiceList();
        NonNullList<RecipeItemStack> data = NonNullList.a(ingredients.size(), RecipeItemStack.a);
        for (int index = 0; index < ingredients.size(); index++) {
            data.set(index, craftRecipe.toNMS(ingredients.get(index), false));
        }

        return new FingerShapelessRecipe(
            recipeKey,
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            CraftItemStack.asNMSCopy(shapelessRecipe.getResult()),
            data
        );
    }

    private static boolean matchesFinger(NamespacedKey recipeKey, InventoryCrafting inventory) {
        List<net.minecraft.world.item.ItemStack> contents = inventory.h();
        org.bukkit.inventory.ItemStack[] matrix = new org.bukkit.inventory.ItemStack[contents.size()];
        for (int index = 0; index < contents.size(); index++) {
            matrix[index] = CraftItemStack.asBukkitCopy(contents.get(index));
        }
        return recipeKey.equals(RecipeFingerManager.INSTANCE.findRecipeByGrid(matrix));
    }

    private static final class FingerShapedRecipe extends ShapedRecipes {

        private final NamespacedKey recipeKey;

        private FingerShapedRecipe(
            NamespacedKey recipeKey,
            MinecraftKey nmsRecipeKey,
            String group,
            CraftingBookCategory category,
            int width,
            int height,
            NonNullList<RecipeItemStack> ingredients,
            net.minecraft.world.item.ItemStack result
        ) {
            super(nmsRecipeKey, group, category, width, height, ingredients, result);
            this.recipeKey = recipeKey;
        }

        @Override
        public boolean a(InventoryCrafting inventory, World world) {
            return matchesFinger(recipeKey, inventory);
        }
    }

    private static final class FingerShapelessRecipe extends ShapelessRecipes {

        private final NamespacedKey recipeKey;

        private FingerShapelessRecipe(
            NamespacedKey recipeKey,
            MinecraftKey nmsRecipeKey,
            String group,
            CraftingBookCategory category,
            net.minecraft.world.item.ItemStack result,
            NonNullList<RecipeItemStack> ingredients
        ) {
            super(nmsRecipeKey, group, category, result, ingredients);
            this.recipeKey = recipeKey;
        }

        @Override
        public boolean a(InventoryCrafting inventory, World world) {
            return matchesFinger(recipeKey, inventory);
        }
    }

}
