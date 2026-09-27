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
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsBrewingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Optional;

public class BrewingRecipe260300 extends BrewingRecipe implements NmsBrewingRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice bukkitInput, bukkitReagent;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    public BrewingRecipe260300(
        NamespacedKey recipeKey,
        PotionIngredient nmsInput,
        RecipeChoice bukkitInput,
        PotionIngredient nmsReagent,
        RecipeChoice bukkitReagent,
        ItemStackTemplate output
    ) {
        super(nmsInput, nmsReagent, output);
        this.recipeKey = recipeKey;
        this.bukkitInput = bukkitInput;
        this.bukkitReagent = bukkitReagent;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public RecipeChoice inputChoice() {
        return bukkitInput;
    }

    @Override
    public RecipeChoice reagentChoice() {
        return bukkitReagent;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey namespacedKey) {
        return cachedBukkitRecipe.get(() -> {
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitMirror(this.getOutput().create());
            return new pers.yufiria.craftorithm.recipe.BrewingRecipe(
                namespacedKey,
                bukkitInput,
                bukkitReagent,
                result
            );
        });
    }

    @Override
    public boolean matches(BrewingInput brewingInput, Level level) {
        return matchesBrewing(CraftItemStack.asBukkitMirror(brewingInput.input()), CraftItemStack.asBukkitMirror(brewingInput.reagent()), level.getWorld());
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static RecipeHolder<BrewingRecipe> fromBukkit(NamespacedKey recipeKey, pers.yufiria.craftorithm.recipe.BrewingRecipe bukkitRecipe) {
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(nmsResult);
        return new RecipeHolder<>(CraftNamespacedKey.toResourceKey(Registries.RECIPE, recipeKey), new BrewingRecipe260300(
            recipeKey,
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
