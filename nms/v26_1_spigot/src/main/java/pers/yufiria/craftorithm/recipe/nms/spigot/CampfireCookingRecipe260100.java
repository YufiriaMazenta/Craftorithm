package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftCampfireRecipe;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

public class CampfireCookingRecipe260100 extends CampfireCookingRecipe {

    private final RecipeChoice ingredient;
    private volatile Recipe cachedBukkitRecipe;

    CampfireCookingRecipe260100(net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result, float exp, int smeltTick) {
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
        CraftCampfireRecipe recipe = new CraftCampfireRecipe(id, result, ingredient, this.experience(), this.cookingTime());
        recipe.setGroup(this.group());
        recipe.setCategory(CraftRecipe.getCategory(this.category()));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<CampfireCookingRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.CampfireRecipe bukkitRecipe) {
        CraftCampfireRecipe craftCampfireRecipe = CraftCampfireRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(bukkitRecipe.getResult()));
        net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo = new net.minecraft.world.item.crafting.Recipe.CommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(CraftRecipe.getCategory(craftCampfireRecipe.getCategory()), craftCampfireRecipe.getGroup());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new CampfireCookingRecipe260100(commonInfo, bookInfo, craftCampfireRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true), recipeChoice, resultTemplate, bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}
