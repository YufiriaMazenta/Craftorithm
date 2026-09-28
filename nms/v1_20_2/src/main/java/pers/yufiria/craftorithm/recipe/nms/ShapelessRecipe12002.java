package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.item.crafting.ShapelessRecipes;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsShapelessRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe12002 extends ShapelessRecipes implements NmsShapelessRecipe<InventoryCrafting> {

    private final NamespacedKey recipeKey;
    private final List<RecipeChoice> customIngredients;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapelessRecipe12002(
        NamespacedKey recipeKey,
        String group,
        CraftingBookCategory category,
        ItemStack result,
        NonNullList<RecipeItemStack> nmsIngredients,
        List<RecipeChoice> customIngredients
    ) {
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
    public BukkitCraftingInput toBukkitCraftingInput(InventoryCrafting craftingInput) {
        List<ItemStack> nmsItems = craftingInput.h();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<>(nmsItems.size());
        for (ItemStack nmsItem : nmsItems) {
            items.add(CraftItemStack.asCraftMirror(nmsItem));
        }
        return new BukkitCraftingInput(items, craftingInput.f(), craftingInput.g());
    }

    @Override
    public boolean a(InventoryCrafting craftingInput, World world) {
        return matches(craftingInput, world.getWorld());
    }

    @Override
    public ShapelessRecipe toBukkitRecipe(NamespacedKey recipeKey) {
        return (ShapelessRecipe) cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            CraftShapelessRecipe recipe = new CraftShapelessRecipe(recipeKey, result, this);
            recipe.setGroup(this.c());
            recipe.setCategory(CraftRecipe.getCategory(this.d()));

            for(RecipeChoice choice : this.customIngredients) {
                recipe.addIngredient(choice);
            }

            return recipe;
        });
    }

    public static RecipeHolder<ShapelessRecipes> fromBukkit(NamespacedKey recipeKey, ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        NonNullList<RecipeItemStack> nmsIngredients = NonNullList.a(bukkitIngredients.size(), RecipeItemStack.a);

        for (int i = 0; i < bukkitIngredients.size(); i++) {
            nmsIngredients.set(i, craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitIngredients.get(i)), true));
        }

        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new ShapelessRecipe12002(
                recipeKey,
                craftRecipe.getGroup(),
                CraftRecipe.getCategory(craftRecipe.getCategory()),
                CraftItemStack.asNMSCopy(craftRecipe.getResult()),
                nmsIngredients,
                bukkitIngredients
            )
        );
    }
}
