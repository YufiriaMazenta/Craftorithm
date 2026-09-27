package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsShapelessRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12110 extends ShapelessRecipe implements NmsShapelessRecipe<CraftingInput> {

    private final NamespacedKey recipeKey;
    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapelessRecipe12110(NamespacedKey recipeKey, String group, CraftingBookCategory category, ItemStack result, List<Ingredient> nmsIngredients, List<RecipeChoice> customIngredients) {
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
        List<ItemStack> nmsItems = craftinginput.items();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<>(nmsItems.size());
        for (ItemStack nmsItem : nmsItems) {
            items.add(CraftItemStack.asCraftMirror(nmsItem));
        }
        return new BukkitCraftingInput(items, craftinginput.width(), craftinginput.height());
    }

    @Override
    public boolean matches(CraftingInput craftinginput, Level level) {
        return matchesShapeless(craftinginput, level.getWorld());
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        return (org.bukkit.inventory.ShapelessRecipe) cachedBukkitRecipe.get(() -> {
            org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
            CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
            recipe.setGroup(this.group());
            recipe.setCategory(CraftRecipe.getCategory(this.category()));
            for (RecipeChoice choice : this.customIngredients) {
                recipe.addIngredient(choice);
            }
            return recipe;
        });
    }

    public static RecipeHolder<ShapelessRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<Ingredient> nmsIngredients = new ArrayList<>(bukkitIngredients.size());
        for (RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(craftRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true));
        }
        ShapelessRecipe nmsRecipe = new ShapelessRecipe12110(recipeKey, craftRecipe.getGroup(), CraftRecipe.getCategory(craftRecipe.getCategory()), CraftItemStack.asNMSCopy(craftRecipe.getResult()), nmsIngredients, bukkitIngredients);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsRecipe);
    }
}
