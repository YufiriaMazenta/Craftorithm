package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftSmithingTransformRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitSmithingInput;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTransformRecipe12103 extends SmithingTransformRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final Optional<RecipeChoice> template, addition;
    private final RecipeChoice base;
    private final ItemStack result;
    private PlacementInfo placementInfo;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTransformRecipe12103(NamespacedKey recipeKey, Optional<RecipeItemStack> nmsTemplate, Optional<RecipeChoice> template, Optional<RecipeItemStack> nmsBase, RecipeChoice base, Optional<RecipeItemStack> nmsAddition, Optional<RecipeChoice> addition, ItemStack result) {
        super(nmsTemplate, nmsBase, nmsAddition, result);
        this.recipeKey = recipeKey;
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.result = result;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public PlacementInfo ap_() {
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
            org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(this.result);
            return new CraftSmithingTransformRecipe(id, result, template.orElse(null), base, addition.orElse(null));
        });
    }

    public static RecipeHolder<SmithingTransformRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTransformRecipe12103(
            recipeKey,
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false),
            Optional.ofNullable(bukkitRecipe.getTemplate()),
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), false),
            bukkitRecipe.getBase(),
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), false),
            Optional.ofNullable(bukkitRecipe.getAddition()),
            nmsResult
        ));
    }
}
