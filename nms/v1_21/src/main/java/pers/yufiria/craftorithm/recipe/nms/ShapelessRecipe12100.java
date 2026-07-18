package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.util.ItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_21_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12100 extends ShapelessRecipes {

    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;

    ShapelessRecipe12100(String group, CraftingBookCategory category, ItemStack result, NonNullList<RecipeItemStack> nmsIngredients, List<RecipeChoice> customIngredients) {
        super(group, category, result, nmsIngredients);
        this.result = result;
        this.customIngredients = customIngredients;
    }

    @Override
    public boolean a(CraftingInput craftinginput, World world) {
        if (craftinginput.a() != this.customIngredients.size()) return false;
        if (craftinginput.a() == 1 && customIngredients.size() == 1) {
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(craftinginput.a(0));
            return customIngredients.getFirst().test(bukkitCopy);
        }
        List<org.bukkit.inventory.ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < craftinginput.a(); i++) {
            ItemStack nmsStack = craftinginput.a(i);
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(nmsStack);
            if (!ItemHelper.isAir(bukkitCopy)) inputItems.add(bukkitCopy);
        }
        return RecipeUtils.matchItemsToChoices(inputItems, customIngredients);
    }

    @Override
    public ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
        recipe.setGroup(this.c());
        recipe.setCategory(CraftRecipe.getCategory(this.d()));
        for (RecipeChoice choice : this.customIngredients) {
            recipe.addIngredient(choice);
        }
        return recipe;
    }

    public static RecipeHolder<ShapelessRecipes> fromBukkit(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        NonNullList<RecipeItemStack> nmsIngredients = NonNullList.a(bukkitIngredients.size(), RecipeItemStack.a);
        for (int i = 0; i < bukkitIngredients.size(); i++) {
            nmsIngredients.set(i, craftRecipe.toNMS(RecipeUtils.getBukkitChoice(bukkitIngredients.get(i)), true));
        }
        ShapelessRecipes nmsRecipe = new ShapelessRecipe12100(
            craftRecipe.getGroup(),
            CraftRecipe.getCategory(craftRecipe.getCategory()),
            CraftItemStack.asNMSCopy(craftRecipe.getResult()),
            nmsIngredients,
            bukkitIngredients
        );
        return new RecipeHolder<>(CraftNamespacedKey.toMinecraft(recipeKey), nmsRecipe);
    }
}
