package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.RecipeBlasting;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftBlastingRecipe;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSingleItemRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class BlastingRecipe12002 extends RecipeBlasting implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final ItemStack result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    BlastingRecipe12002(
        NamespacedKey recipeKey,
        String group,
        CookingBookCategory cookingbookcategory,
        RecipeItemStack nmsIngredient,
        RecipeChoice ingredient,
        ItemStack result,
        float exp,
        int smeltTick
    ) {
        super(group, cookingbookcategory, nmsIngredient, result, exp, smeltTick);
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
    public Recipe toBukkitRecipe(NamespacedKey recipeKey) {
        return cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
            CraftBlastingRecipe recipe = new CraftBlastingRecipe(recipeKey, result, ingredient, this.b(), this.d());
            recipe.setGroup(this.c());
            recipe.setCategory(CraftRecipe.getCategory(this.b));
            return recipe;
        });
    }

    public static RecipeHolder<RecipeBlasting> fromBukkit(NamespacedKey recipeKey, BlastingRecipe bukkitRecipe) {
        CraftBlastingRecipe craftBlastingRecipe = CraftBlastingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        return new RecipeHolder<>(
            CraftNamespacedKey.toMinecraft(recipeKey),
            new BlastingRecipe12002(
                recipeKey,
                craftBlastingRecipe.getGroup(),
                CraftRecipe.getCategory(craftBlastingRecipe.getCategory()),
                craftBlastingRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true),
                recipeChoice,
                CraftItemStack.asNMSCopy(bukkitRecipe.getResult()),
                bukkitRecipe.getExperience(),
                bukkitRecipe.getCookingTime()
            )
        );
    }

}
