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
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class SmeltingRecipe260100 extends SmeltingRecipe {

    private final RecipeChoice ingredient;
    private volatile Recipe cachedBukkitRecipe;

    public SmeltingRecipe260100(net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result, float exp, int smeltTick) {
        super(commonInfo, bookInfo, nmsIngredient, result, exp, smeltTick);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.item()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result().create());
        CraftFurnaceRecipe recipe = new CraftFurnaceRecipe(id, result, ingredient, this.experience(), this.cookingTime());
        recipe.setGroup(this.group());
        recipe.setCategory(CraftRecipe.getCategory(this.category()));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<SmeltingRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.FurnaceRecipe bukkitRecipe) {
        CraftFurnaceRecipe craftFurnaceRecipe = CraftFurnaceRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(bukkitRecipe.getResult()));
        net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo = new net.minecraft.world.item.crafting.Recipe.CommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(CraftRecipe.getCategory(craftFurnaceRecipe.getCategory()), craftFurnaceRecipe.getGroup());
        return new RecipeHolder<>(
            CraftNamespacedKey.toResourceKey(Registries.RECIPE, recipeKey),
            new SmeltingRecipe260100(
                commonInfo,
                bookInfo,
                CraftRecipe.toIngredient(RecipeUtils.getBukkitChoice(recipeChoice), true),
                recipeChoice,
                resultTemplate,
                bukkitRecipe.getExperience(),
                bukkitRecipe.getCookingTime()
            )
        );
    }
}
