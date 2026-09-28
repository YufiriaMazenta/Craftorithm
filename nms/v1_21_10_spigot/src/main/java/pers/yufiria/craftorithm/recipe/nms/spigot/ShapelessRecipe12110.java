package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsShapelessRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12110 extends ShapelessRecipes implements NmsShapelessRecipe<CraftingInput> {

    private final NamespacedKey recipeKey;
    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapelessRecipe12110(NamespacedKey recipeKey, String group, CraftingBookCategory category, ItemStack result, List<RecipeItemStack> nmsIngredients, List<RecipeChoice> customIngredients) {
        super(group, category, result, nmsIngredients);
        this.recipeKey = recipeKey;
        this.result = result;
        this.customIngredients = customIngredients;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public List<RecipeChoice> customIngredients() {
        return customIngredients;
    }

    @Override
    public BukkitCraftingInput toBukkitCraftingInput(CraftingInput craftinginput) {
        List<ItemStack> nmsItems = craftinginput.d();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<>(nmsItems.size());
        for (ItemStack nmsItem : nmsItems) {
            items.add(CraftItemStack.asCraftMirror(nmsItem));
        }
        return new BukkitCraftingInput(items, craftinginput.f(), craftinginput.g(), craftinginput.e());
    }

    @Override
    public boolean a(CraftingInput craftinginput, World world) {
        return matches(craftinginput, world.getWorld());
    }

    @Override
    public ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        return (ShapelessRecipe) cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
            recipe.setGroup(this.j());
            recipe.setCategory(CraftRecipe.getCategory(this.c()));
            for (RecipeChoice choice : this.customIngredients) {
                recipe.addIngredient(choice);
            }
            return recipe;
        });
    }

    public static RecipeHolder<ShapelessRecipes> fromBukkit(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<RecipeItemStack> nmsIngredients = new ArrayList<>(bukkitIngredients.size());
        for (RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(craftRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true));
        }
        ShapelessRecipes nmsRecipe = new ShapelessRecipe12110(recipeKey, craftRecipe.getGroup(), CraftRecipe.getCategory(craftRecipe.getCategory()), CraftItemStack.asNMSCopy(craftRecipe.getResult()), nmsIngredients, bukkitIngredients);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsRecipe);
    }
}
