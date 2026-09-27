package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.BukkitCraftingInput;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsShapelessRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe260100 extends ShapelessRecipe implements NmsShapelessRecipe<CraftingInput> {

    private final NamespacedKey recipeKey;
    private final List<RecipeChoice> customIngredients;
    private final ItemStackTemplate result;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    ShapelessRecipe260100(NamespacedKey recipeKey, Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> nmsIngredients, List<RecipeChoice> customIngredients) {
        super(commonInfo, bookInfo, result, nmsIngredients);
        this.recipeKey = recipeKey;
        this.customIngredients = customIngredients;
        this.result = result;
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
    public BukkitCraftingInput toBukkitCraftingInput(CraftingInput craftinginput) {
        List<net.minecraft.world.item.ItemStack> nmsItems = craftinginput.items();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<>(nmsItems.size());
        for (net.minecraft.world.item.ItemStack nmsItem : nmsItems) {
            items.add(CraftItemStack.asCraftMirror(nmsItem));
        }
        return new BukkitCraftingInput(items, craftinginput.width(), craftinginput.height());
    }

    @Override
    public boolean matches(CraftingInput craftinginput, Level level) {
        return matchesShapeless(craftinginput, level.getWorld());
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        return (org.bukkit.inventory.ShapelessRecipe) cachedBukkitRecipe.get(() -> {
            org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result.create());
            CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
            recipe.setGroup(this.group());
            recipe.setCategory(CraftRecipe.getCategory(this.category()));
            for (RecipeChoice choice : this.customIngredients) {
                recipe.addIngredient(choice);
            }
            return recipe;
        });
    }

    public static RecipeHolder<ShapelessRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<Ingredient> nmsIngredients = new ArrayList<>(bukkitIngredients.size());
        for (RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(CraftRecipe.toIngredient(IngredientUtils.getBukkitChoice(recipeChoice), true));
        }
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(craftRecipe.getResult()));
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        CraftingRecipe.CraftingBookInfo bookInfo = new CraftingRecipe.CraftingBookInfo(CraftRecipe.getCategory(craftRecipe.getCategory()), craftRecipe.getGroup());
        ShapelessRecipe nmsRecipe = new ShapelessRecipe260100(recipeKey, commonInfo, bookInfo, resultTemplate, nmsIngredients, bukkitIngredients);
        return new RecipeHolder<>(CraftNamespacedKey.toResourceKey(Registries.RECIPE, recipeKey), nmsRecipe);
    }
}
