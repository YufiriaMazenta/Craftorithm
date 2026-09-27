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
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSingleItemRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

public class CampfireCookingRecipe260100 extends CampfireCookingRecipe implements NmsSingleItemRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice ingredient;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    CampfireCookingRecipe260100(NamespacedKey recipeKey, net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo, AbstractCookingRecipe.CookingBookInfo bookInfo, Ingredient nmsIngredient, RecipeChoice ingredient, ItemStackTemplate result, float exp, int smeltTick) {
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
            CraftCampfireRecipe recipe = new CraftCampfireRecipe(id, result, ingredient, this.experience(), this.cookingTime());
            recipe.setGroup(this.group());
            recipe.setCategory(CraftRecipe.getCategory(this.category()));
            return recipe;
        });
    }

    public static RecipeHolder<CampfireCookingRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.CampfireRecipe bukkitRecipe) {
        CraftCampfireRecipe craftCampfireRecipe = CraftCampfireRecipe.fromBukkitRecipe(bukkitRecipe);
        RecipeChoice recipeChoice = bukkitRecipe.getInputChoice();
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(bukkitRecipe.getResult()));
        net.minecraft.world.item.crafting.Recipe.CommonInfo commonInfo = new net.minecraft.world.item.crafting.Recipe.CommonInfo(true);
        AbstractCookingRecipe.CookingBookInfo bookInfo = new AbstractCookingRecipe.CookingBookInfo(CraftRecipe.getCategory(craftCampfireRecipe.getCategory()), craftCampfireRecipe.getGroup());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new CampfireCookingRecipe260100(recipeKey, commonInfo, bookInfo, craftCampfireRecipe.toNMS(IngredientUtils.getBukkitChoice(recipeChoice), true), recipeChoice, resultTemplate, bukkitRecipe.getExperience(), bukkitRecipe.getCookingTime()));
    }
}
