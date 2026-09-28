package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.NmsSingleItemRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class SmeltingRecipe260300 extends SmeltingRecipe implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    public SmeltingRecipe260300(NamespacedKey recipeKey, net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result, float exp, int smeltTick) {
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
        return matches(CraftItemStack.asBukkitMirror(input.item()), level.getWorld());
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            ItemStack result = CraftItemStack.asBukkitMirror(this.result().create());
            CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(id, result, ingredient, this.experience(), this.cookingTime());
            recipe.setGroup(this.group());
            recipe.setCategory(CraftRecipe.getCategory(this.category()));
            return recipe;
        });
    }

    public static RecipeHolder<SmeltingRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.FurnaceRecipe bukkitRecipe) {
        CraftFurnaceRecipe craftFurnaceRecipe = CraftFurnaceRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(bukkitRecipe.getResult()));
        net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo = new net.minecraft.world.item.crafting.Recipe.CommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()), craftFurnaceRecipe.getGroup());
        return new RecipeHolder<>(
            CraftNamespacedKey.toResourceKey(Registries.RECIPE, recipeKey),
            new SmeltingRecipe260300(
                recipeKey,
                commonInfo,
                bookInfo,
                CraftRecipe.toIngredient(IngredientUtils.getBukkitChoice(recipeChoice), true),
                recipeChoice,
                resultTemplate,
                bukkitRecipe.getExperience(),
                bukkitRecipe.getCookingTime()
            )
        );
    }
}
