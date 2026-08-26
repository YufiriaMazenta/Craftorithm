package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.util.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.ShapelessRecipes;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12000 extends ShapelessRecipes {

    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;
    private volatile Recipe cachedBukkitRecipe;

    ShapelessRecipe12000(
        MinecraftKey recipeKey,
        String group,
        CraftingBookCategory category,
        ItemStack result,
        NonNullList<RecipeItemStack> nmsIngredients,
        List<RecipeChoice> customIngredients
    ) {
        super(recipeKey, group, category, result, nmsIngredients);
        this.result = result;
        this.customIngredients = customIngredients;
    }

    @Override
    public boolean a(InventoryCrafting craftingInput, World world) {
        List<ItemStack> nmsInputItems = craftingInput.h();
        List<org.bukkit.inventory.ItemStack> inputItems = new ArrayList<>(nmsInputItems.size());
        for (ItemStack nmsStack : nmsInputItems) {
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(nmsStack);
            if (!ItemHelper.isAir(bukkitCopy)) {
                inputItems.add(bukkitCopy);
            }
        }
        if (inputItems.size() != customIngredients.size()) {
            return false;
        }
        return RecipeUtils.matchItemsToChoices(inputItems, customIngredients);
    }

    @Override
    public ShapelessRecipe toBukkitRecipe() {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return (ShapelessRecipe) cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapelessRecipe recipe = new CraftShapelessRecipe(result, this);
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.d()));

        for(RecipeChoice choice : this.customIngredients) {
            recipe.addIngredient(choice);
        }

        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static ShapelessRecipes fromBukkit(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        NonNullList<RecipeItemStack> nmsIngredients = NonNullList.a(bukkitIngredients.size(), RecipeItemStack.a);

        for (int i = 0; i < bukkitIngredients.size(); i++) {
            nmsIngredients.set(i, craftRecipe.toNMS(RecipeUtils.getBukkitChoice(bukkitIngredients.get(i)), true));
        }

        return new ShapelessRecipe12000(
            CraftNamespacedKey.toMinecraft(recipeKey),
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            CraftItemStack.asNMSCopy(craftRecipe.getResult()),
            nmsIngredients,
            bukkitIngredients
        );
    }
}
