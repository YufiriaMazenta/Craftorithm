package pers.yufiria.craftorithm.recipe.nms.spigot;

import crypticlib.util.ItemHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.ArrayList;
import java.util.List;

public class ShapelessRecipe260100 extends ShapelessRecipe {

    private final List<RecipeChoice> customIngredients;
    private final ItemStackTemplate result;
    private volatile org.bukkit.inventory.Recipe cachedBukkitRecipe;

    ShapelessRecipe260100(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> nmsIngredients, List<RecipeChoice> customIngredients) {
        super(commonInfo, bookInfo, result, nmsIngredients);
        this.customIngredients = customIngredients;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingInput craftinginput, Level level) {
        List<ItemStack> inputItems = craftinginput.items();
        List<org.bukkit.inventory.ItemStack> inputBukkitItems = new ArrayList<>();
        for (ItemStack nmsInputItem : inputItems) {
            org.bukkit.inventory.ItemStack bukkitCopy = CraftItemStack.asCraftMirror(nmsInputItem);
            if (!ItemHelper.isAir(bukkitCopy)) inputBukkitItems.add(bukkitCopy);
        }
        if (inputBukkitItems.size() != this.customIngredients.size()) return false;
        if (inputBukkitItems.size() == 1) {
            return customIngredients.getFirst().test(inputBukkitItems.getFirst());
        }
        return RecipeUtils.matchItemsToChoices(inputBukkitItems, customIngredients);
    }

    @Override
    public org.bukkit.inventory.ShapelessRecipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return (org.bukkit.inventory.ShapelessRecipe) cached;
        }
        org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
        CraftShapelessRecipe recipe = new CraftShapelessRecipe(id, result, this);
        recipe.setGroup(this.group());
        recipe.setCategory(CraftRecipe.getCategory(this.category()));
        for (RecipeChoice choice : this.customIngredients) {
            recipe.addIngredient(choice);
        }
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<ShapelessRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.ShapelessRecipe shapelessRecipe) {
        CraftShapelessRecipe craftRecipe = CraftShapelessRecipe.fromBukkitRecipe(shapelessRecipe);
        List<RecipeChoice> bukkitIngredients = craftRecipe.getChoiceList();
        List<Ingredient> nmsIngredients = new ArrayList<>(bukkitIngredients.size());
        for (RecipeChoice recipeChoice : bukkitIngredients) {
            nmsIngredients.add(craftRecipe.toNMS(RecipeUtils.getBukkitChoice(recipeChoice), true));
        }
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(CraftItemStack.asNMSCopy(craftRecipe.getResult()));
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        CraftingRecipe.CraftingBookInfo bookInfo = new CraftingRecipe.CraftingBookInfo(CraftRecipe.getCategory(craftRecipe.getCategory()), craftRecipe.getGroup());
        ShapelessRecipe nmsRecipe = new ShapelessRecipe260100(commonInfo, bookInfo, resultTemplate, nmsIngredients, bukkitIngredients);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), nmsRecipe);
    }
}
