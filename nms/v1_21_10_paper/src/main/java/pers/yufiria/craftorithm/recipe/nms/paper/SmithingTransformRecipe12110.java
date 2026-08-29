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
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTransformRecipe12110 extends SmithingTransformRecipe {

    private final Optional<RecipeChoice> template, addition;
    private final RecipeChoice base;
    private final TransmuteResult result;
    private PlacementInfo placementInfo;
    private volatile Recipe cachedBukkitRecipe;

    SmithingTransformRecipe12110(Optional<Ingredient> nmsTemplate, Optional<RecipeChoice> template, Ingredient nmsBase, RecipeChoice base, Optional<Ingredient> nmsAddition, Optional<RecipeChoice> addition, TransmuteResult transmuteresult) {
        super(nmsTemplate, nmsBase, nmsAddition, transmuteresult);
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.result = transmuteresult;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.empty()));
        }
        return placementInfo;
    }

    @Override
    public boolean matches(SmithingRecipeInput smithingInput, Level level) {
        return IngredientUtils.testOptionalChoice(template, CraftItemStack.asCraftMirror(smithingInput.template()))
            && base.test(CraftItemStack.asCraftMirror(smithingInput.base()))
            && IngredientUtils.testOptionalChoice(addition, CraftItemStack.asCraftMirror(smithingInput.addition()));
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        net.minecraft.world.item.ItemStack nms = new ItemStack(result.item(), result.count(), result.components());
        org.bukkit.inventory.ItemStack result = CraftItemStack.asBukkitCopy(nms);
        CraftSmithingTransformRecipe recipe = new CraftSmithingTransformRecipe(id, result, template.orElse(null), base, addition.orElse(null));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<SmithingTransformRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        TransmuteResult transmuteResult = new TransmuteResult(nmsResult.getItemHolder(), nmsResult.getCount(), nmsResult.getComponentsPatch());
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTransformRecipe12110(craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false), Optional.ofNullable(bukkitRecipe.getTemplate()), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(bukkitRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMSOptional(IngredientUtils.getBukkitChoice(bukkitRecipe.getAddition()), false), Optional.ofNullable(bukkitRecipe.getAddition()), transmuteResult));
    }
}
