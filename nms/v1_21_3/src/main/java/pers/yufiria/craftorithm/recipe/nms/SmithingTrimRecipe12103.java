package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftSmithingTrimRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSmithingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe12103 extends SmithingTrimRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final Optional<RecipeChoice> template, addition;
    private final Optional<RecipeChoice> base;
    private PlacementInfo placementInfo;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTrimRecipe12103(NamespacedKey recipeKey, Optional<RecipeItemStack> nmsTemplate, Optional<RecipeChoice> template, Optional<RecipeItemStack> nmsBase, Optional<RecipeChoice> base, Optional<RecipeItemStack> nmsAddition, Optional<RecipeChoice> addition) {
        super(nmsTemplate, nmsBase, nmsAddition);
        this.recipeKey = recipeKey;
        this.template = template;
        this.addition = addition;
        this.base = base;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public Optional<RecipeChoice> templateChoice() {
        return template;
    }

    @Override
    public Optional<RecipeChoice> baseChoice() {
        return base;
    }

    @Override
    public Optional<RecipeChoice> additionChoice() {
        return addition;
    }

    @Override
    public boolean a(SmithingRecipeInput smithingInput, World world) {
        return matchesSmithing(
            CraftItemStack.asCraftMirror(smithingInput.c()),
            CraftItemStack.asCraftMirror(smithingInput.d()),
            CraftItemStack.asCraftMirror(smithingInput.e()),
            world.getWorld()
        );
    }

    @Override
    public PlacementInfo ap_() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.a(List.of(Optional.empty()));
        }
        return placementInfo;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            Recipe recipe = new CraftSmithingTrimRecipe(id, template.orElse(null), base.orElse(null), addition.orElse(null));
            return recipe;
        });
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTrimRecipe12103(
            recipeKey,
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false),
            Optional.ofNullable(bukkitRecipe.getTemplate()),
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), false),
            Optional.ofNullable(bukkitRecipe.getBase()),
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), false),
            Optional.ofNullable(bukkitRecipe.getAddition())
        ));
    }
}
