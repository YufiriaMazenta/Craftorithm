package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftBlastingRecipe;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSingleItemRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class BlastingRecipe12111 extends RecipeBlasting implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    BlastingRecipe12111(NamespacedKey recipeKey, String group, CookingBookCategory cookingbookcategory, RecipeItemStack recipeitemstack, RecipeChoice ingredient, ItemStack result, float exp, int smeltTick) {
        super(group, cookingbookcategory, recipeitemstack, result, exp, smeltTick);
        this.recipeKey = recipeKey;
        this.ingredient = ingredient;
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
    public boolean a(SingleRecipeInput input, World world) {
        return matchesIngredient(CraftItemStack.asCraftMirror(input.c()), world.getWorld());
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.l());
            CraftBlastingRecipe recipe = new CraftBlastingRecipe(id, result, ingredient, this.c(), this.d());
            recipe.setGroup(this.j());
            recipe.setCategory(CraftRecipe.getCategory(this.e()));
            return recipe;
        });
    }

    public static RecipeHolder<RecipeBlasting> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.BlastingRecipe bukkitRecipe) {
        CraftBlastingRecipe craftBlastingRecipe = CraftBlastingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new BlastingRecipe12111(recipeKey, craftBlastingRecipe.getGroup(), CraftRecipe.getCategory(craftBlastingRecipe.getCategory()), craftBlastingRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true), recipeChoice, CraftItemStack.asNMSCopy(bukkitRecipe.getResult()), bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}


