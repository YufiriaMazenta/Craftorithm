package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeBlasting;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftBlastingRecipe;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSingleItemRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class BlastingRecipe12000 extends RecipeBlasting implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    BlastingRecipe12000(
        NamespacedKey recipeKey,
        String group,
        CookingBookCategory cookingbookcategory,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result,
        float exp,
        int smeltTick
    ) {
        super(CraftNamespacedKey.toMinecraft(recipeKey), group, cookingbookcategory, nmsIngredient, result, exp, smeltTick);
        this.recipeKey = recipeKey;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public RecipeChoice ingredientChoice() {
        return ingredient;
    }

    @Override
    public boolean a(IInventory input, World world) {
        return matchesIngredient(CraftItemStack.asCraftMirror(input.a(0)), world.getWorld());
    }

    @Override
    public Recipe toBukkitRecipe() {
        return cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            CraftBlastingRecipe recipe = new CraftBlastingRecipe(CraftNamespacedKey.fromMinecraft(this.e()), result, ingredient, this.b(), this.d());
            recipe.setGroup(this.c());
            recipe.setCategory(CraftRecipe.getCategory(this.g()));
            return recipe;
        });
    }

    public static RecipeBlasting fromBukkit(NamespacedKey recipeKey, BlastingRecipe bukkitRecipe) {
        CraftBlastingRecipe craftFurnaceRecipe = CraftBlastingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new BlastingRecipe12000(
            recipeKey,
            craftFurnaceRecipe.getGroup(),
            CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()),
            craftFurnaceRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true),
            recipeChoice,
            CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
            bukkitRecipe.getExperience(),
            bukkitRecipe.getCookingTime()
        );
    }

}
