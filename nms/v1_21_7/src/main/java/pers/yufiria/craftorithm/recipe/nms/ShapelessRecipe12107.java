package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12107 extends ShapelessRecipes {

    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    ShapelessRecipe12107(
        String group,
        CraftingBookCategory category,
        ItemStack result,
        List<RecipeItemStack> nmsIngredients,
        List<RecipeChoice> customIngredients
    ) {
        super(group, category, result, nmsIngredients);
        this.result = result;
        this.customIngredients = customIngredients;
    }

    @Override
    public boolean a(CraftingInput craftinginput, World world) {
        List<ItemStack> inputItems = craftinginput.d();
        List<org.bukkit.inventory.ItemStack> inputBukkitItems = new ArrayList<>(inputItems.size());
        for (ItemStack nmsInputItem : inputItems) {
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(nmsInputItem);
            if (!ItemHelper.isAir(bukkitCopy)) inputBukkitItems.add(bukkitCopy);
        }
        if (inputBukkitItems.size() != this.customIngredients.size()) return false;
        if (inputBukkitItems.size() == 1) {
            return customIngredients.getFirst().test(inputBukkitItems.getFirst());
        }
        return IngredientUtils.matchItemsToChoices(inputBukkitItems, customIngredients);
    }

    @Override
    public ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return (ShapelessRecipe) cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
        recipe.setGroup(this.j());
        recipe.setCategory(CraftRecipe.getCategory(this.c()));

        for(RecipeChoice choice : this.customIngredients) {
            recipe.addIngredient(choice);
        }

        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<ShapelessRecipes> fromBukkit(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<RecipeItemStack> nmsIngredients = new ArrayList<>(bukkitIngredients.size());

        for (RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(craftRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true));
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
