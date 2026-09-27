package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftBlastingRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSingleItemRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class BlastingRecipe260100 extends BlastingRecipe implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    BlastingRecipe260100(NamespacedKey recipeKey, net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result, float exp, int smeltTick) {
        super(commonInfo, bookInfo, nmsIngredient, result, exp, smeltTick);
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
    public boolean matches(SingleRecipeInput input, Level level) {
        return matchesIngredient(CraftItemStack.asCraftMirror(input.item()), level.getWorld());
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            CraftItemStack result = CraftItemStack.asCraftMirror(this.result().create());
            CraftBlastingRecipe recipe = new CraftBlastingRecipe(id, result, ingredient, this.experience(), this.cookingTime());
            recipe.setGroup(this.group());
            recipe.setCategory(CraftRecipe.getCategory(this.category()));
            return recipe;
        });
    }

    public static RecipeHolder<BlastingRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.BlastingRecipe bukkitRecipe) {
        CraftBlastingRecipe craftBlastingRecipe = CraftBlastingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(bukkitRecipe.getResult()));
        net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo = new net.minecraft.world.item.crafting.Recipe.CommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(CraftRecipe.getCategory(craftBlastingRecipe.getCategory()), craftBlastingRecipe.getGroup());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new BlastingRecipe260100(recipeKey, commonInfo, bookInfo, craftBlastingRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true), recipeChoice, resultTemplate, bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}
