package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmokingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class SmokingRecipe260100 extends SmokingRecipe {

    private final RecipeChoice ingredient;

    SmokingRecipe260100(net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result, float exp, int smeltTick) {
        super(commonInfo, bookInfo, nmsIngredient, result, exp, smeltTick);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(CraftItemStack.asCraftMirror(input.item()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result());
        CraftSmokingRecipe recipe = new CraftSmokingRecipe(id, result, ingredient, this.experience(), this.cookingTime());
        recipe.setGroup(this.group());
        recipe.setCategory(CraftRecipe.getCategory(this.category()));
        return recipe;
    }

    public static RecipeHolder<SmokingRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmokingRecipe bukkitRecipe) {
        CraftSmokingRecipe craftSmokingRecipe = CraftSmokingRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(bukkitRecipe.getResult()));
        net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo = new net.minecraft.world.item.crafting.Recipe.CommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(CraftRecipe.getCategory(craftSmokingRecipe.getCategory()), craftSmokingRecipe.getGroup());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmokingRecipe260100(commonInfo, bookInfo, craftSmokingRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true), recipeChoice, resultTemplate, bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}
