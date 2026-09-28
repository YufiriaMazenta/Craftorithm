package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftSmithingTransformRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitSmithingInput;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTransformRecipe12105 extends SmithingTransformRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final Optional<RecipeChoice> template, addition;
    private final RecipeChoice base;
    private final TransmuteResult result;
    private PlacementInfo placementInfo;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTransformRecipe12105(NamespacedKey recipeKey, Optional<RecipeItemStack> nmsTemplate, Optional<RecipeChoice> template, RecipeItemStack nmsBase, RecipeChoice base, Optional<RecipeItemStack> nmsAddition, Optional<RecipeChoice> addition, TransmuteResult transmuteresult) {
        super(nmsTemplate, nmsBase, nmsAddition, transmuteresult);
        this.recipeKey = recipeKey;
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.result = transmuteresult;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public PlacementInfo al_() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.a(List.of(Optional.empty()));
        }
        return placementInfo;
    }

    @Override
    public Optional<RecipeChoice> templateChoice() {
        return template;
    }

    @Override
    public Optional<RecipeChoice> baseChoice() {
        return Optional.ofNullable(base);
    }

    @Override
    public Optional<RecipeChoice> additionChoice() {
        return addition;
    }

    @Override
    public boolean a(SmithingRecipeInput smithingInput, World world) {
        return matches(
            new BukkitSmithingInput(
                CraftItemStack.asCraftMirror(smithingInput.c()),
                CraftItemStack.asCraftMirror(smithingInput.d()),
                CraftItemStack.asCraftMirror(smithingInput.e())
            ),
            world.getWorld()
        );
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            net.minecraft.world.item.ItemStack nms = new net.minecraft.world.item.ItemStack(result.b(), result.c(), result.d());
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(nms);
            return new CraftSmithingTransformRecipe(id, result, template.orElse(null), base, addition.orElse(null));
        });
    }

    public static RecipeHolder<SmithingTransformRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        TransmuteResult transmuteResult = new TransmuteResult(nmsResult.i(), nmsResult.M(), nmsResult.d());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTransformRecipe12105(recipeKey, craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false), Optional.ofNullable(bukkitRecipe.getTemplate()), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), false), Optional.ofNullable(bukkitRecipe.getAddition()), transmuteResult));
    }
}

