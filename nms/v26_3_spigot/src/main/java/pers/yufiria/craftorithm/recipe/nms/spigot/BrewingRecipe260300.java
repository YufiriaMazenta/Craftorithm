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
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsBrewingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Optional;

public class BrewingRecipe260300 extends BrewingRecipe implements NmsBrewingRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice bukkitInput, bukkitReagent;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();
    static CraftRecipe toolCraftRecipe = new CraftRecipe() {
        @Override
        public org.bukkit.inventory.@NotNull ItemStack getResult() {return null;}
        @Override
        public void addToCraftingManager() {}
    };

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
            org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.getOutput());
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
        return matchesBrewing(CraftItemStack.asCraftMirror(brewingInput.input()), CraftItemStack.asCraftMirror(brewingInput.reagent()), level.getWorld());
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static RecipeHolder<BrewingRecipe> fromBukkit(NamespacedKey recipeKey, pers.yufiria.craftorithm.recipe.BrewingRecipe bukkitRecipe) {
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(nmsResult);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new BrewingRecipe260300(
            recipeKey,
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
