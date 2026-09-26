package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Optional;

public class BrewingRecipe260300 extends BrewingRecipe {

    private final RecipeChoice bukkitInput, bukkitReagent;
    private volatile Recipe cachedBukkitRecipe;

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
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitMirror(this.getOutput().create());
        Recipe recipe = new pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe(
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
        return bukkitInput.test(CraftItemStack.asBukkitMirror(brewingInput.input()))
            && bukkitReagent.test(CraftItemStack.asBukkitMirror(brewingInput.reagent()));
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static RecipeHolder<BrewingRecipe> fromBukkit(NamespacedKey recipeKey, pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe bukkitRecipe) {
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(nmsResult);
        return new RecipeHolder<>(CraftNamespacedKey.toResourceKey(Registries.RECIPE, recipeKey), new BrewingRecipe260300(
            new PotionIngredient(
                CraftRecipe.toIngredient(IngredientUtils.getBukkitChoice(bukkitRecipe.input()), false),
                Optional.empty()
            ),
            bukkitRecipe.input(),
            new PotionIngredient(
                CraftRecipe.toIngredient(IngredientUtils.getBukkitChoice(bukkitRecipe.ingredient()), false),
                Optional.empty()
            ),
            bukkitRecipe.ingredient(),
            resultTemplate));
    }

}
