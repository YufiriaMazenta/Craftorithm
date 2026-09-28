package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmithingTransformRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.NmsSmithingRecipe;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitSmithingInput;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTransformRecipe12110 extends SmithingTransformRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final Optional<RecipeChoice> template, addition;
    private final RecipeChoice base;
    private final TransmuteResult result;
    private PlacementInfo placementInfo;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTransformRecipe12110(NamespacedKey recipeKey, Optional<Ingredient> nmsTemplate, Optional<RecipeChoice> template, Ingredient nmsBase, RecipeChoice base, Optional<Ingredient> nmsAddition, Optional<RecipeChoice> addition, TransmuteResult transmuteresult) {
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
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.empty()));
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
    public boolean matches(SmithingRecipeInput smithingInput, Level level) {
        return matches(
            new BukkitSmithingInput(
                CraftItemStack.asCraftMirror(smithingInput.template()),
                CraftItemStack.asCraftMirror(smithingInput.base()),
                CraftItemStack.asCraftMirror(smithingInput.addition())
            ),
            level.getWorld()
        );
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            net.minecraft.world.item.ItemStack nms = new ItemStack(result.item(), result.count(), result.components());
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(nms);
            return new CraftSmithingTransformRecipe(id, result, template.orElse(null), base, addition.orElse(null));
        });
    }

    public static RecipeHolder<SmithingTransformRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        TransmuteResult transmuteResult = new TransmuteResult(nmsResult.getItemHolder(), nmsResult.getCount(), nmsResult.getComponentsPatch());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTransformRecipe12110(recipeKey, craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false), Optional.ofNullable(bukkitRecipe.getTemplate()), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), false), Optional.ofNullable(bukkitRecipe.getAddition()), transmuteResult));
    }
}
