package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Optional;

public class BrewingRecipe260300 extends BrewingRecipe {

    private final RecipeChoice bukkitInput, bukkitReagent;
    private volatile org.bukkit.inventory.Recipe cachedBukkitRecipe;
    static CraftRecipe toolCraftRecipe = new CraftRecipe() {
        @Override
        public org.bukkit.inventory.@NotNull ItemStack getResult() {return null;}
        @Override
        public void addToCraftingManager() {}
    };

    public BrewingRecipe260300(
        PotionIngredient nmsInput,
        RecipeChoice bukkitInput,
        PotionIngredient nmsReagent,
        RecipeChoice bukkitReagent,
        ItemStackTemplate output
    ) {
        super(nmsInput, nmsReagent, output);
        this.bukkitInput = bukkitInput;
        this.bukkitReagent = bukkitReagent;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey namespacedKey) {
        org.bukkit.inventory.Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.getOutput());
        org.bukkit.inventory.Recipe recipe = new pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe(
            namespacedKey,
            bukkitInput,
            bukkitReagent,
            result
        );
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    @Override
    public boolean matches(BrewingInput brewingInput, Level level) {
        return bukkitInput.test(CraftItemStack.asCraftMirror(brewingInput.input()))
            && bukkitReagent.test(CraftItemStack.asCraftMirror(brewingInput.reagent()));
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static RecipeHolder<BrewingRecipe> fromBukkit(NamespacedKey recipeKey, pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe bukkitRecipe) {
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(nmsResult);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new BrewingRecipe260300(
            new PotionIngredient(
                toolCraftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.input()), false),
                Optional.empty()
            ),
            bukkitRecipe.input(),
            new PotionIngredient(
                toolCraftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.ingredient()), false),
                Optional.empty()
            ),
            bukkitRecipe.ingredient(),
            resultTemplate));
    }

}
