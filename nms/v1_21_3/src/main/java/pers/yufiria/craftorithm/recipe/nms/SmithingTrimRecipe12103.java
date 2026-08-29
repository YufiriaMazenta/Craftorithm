package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftSmithingTrimRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe12103 extends SmithingTrimRecipe {

    private final Optional<RecipeChoice> template, addition;
    private final Optional<RecipeChoice> base;
    private PlacementInfo placementInfo;
    private volatile Recipe cachedBukkitRecipe;

    SmithingTrimRecipe12103(Optional<RecipeItemStack> nmsTemplate, Optional<RecipeChoice> template, Optional<RecipeItemStack> nmsBase, Optional<RecipeChoice> base, Optional<RecipeItemStack> nmsAddition, Optional<RecipeChoice> addition) {
        super(nmsTemplate, nmsBase, nmsAddition);
        this.template = template;
        this.addition = addition;
        this.base = base;
    }

    @Override
    public boolean a(SmithingRecipeInput smithingInput, World world) {
        return IngredientUtils.testOptionalChoice(template, CraftItemStack.asCraftMirror(smithingInput.c()))
            && IngredientUtils.testOptionalChoice(base, CraftItemStack.asCraftMirror(smithingInput.d()))
            && IngredientUtils.testOptionalChoice(addition, CraftItemStack.asCraftMirror(smithingInput.e()));
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
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        Recipe recipe = new CraftSmithingTrimRecipe(id, template.orElse(null), base.orElse(null), addition.orElse(null));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTrimRecipe12103(
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false),
            Optional.ofNullable(bukkitRecipe.getTemplate()),
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), false),
            Optional.ofNullable(bukkitRecipe.getBase()),
            craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), false),
            Optional.ofNullable(bukkitRecipe.getAddition())
        ));
    }
}
