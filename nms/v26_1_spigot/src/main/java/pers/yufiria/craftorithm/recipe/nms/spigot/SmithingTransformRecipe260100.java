package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmithingTransformRecipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTransformRecipe260100 extends SmithingTransformRecipe {

    private final Optional<RecipeChoice> template, addition;
    private final RecipeChoice base;
    private final ItemStackTemplate result;
    private PlacementInfo placementInfo;

    SmithingTransformRecipe260100(Recipe.CommonInfo commonInfo, Optional<Ingredient> nmsTemplate, Optional<RecipeChoice> template, Ingredient nmsBase, RecipeChoice base, Optional<Ingredient> nmsAddition, Optional<RecipeChoice> addition, ItemStackTemplate result) {
        super(commonInfo, nmsTemplate, nmsBase, nmsAddition, result);
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.result = result;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.empty()));
        }
        return super.placementInfo();
    }

    @Override
    public boolean matches(SmithingRecipeInput smithingInput, Level level) {
        return RecipeUtils.testOptionalChoice(template, CraftItemStack.asCraftMirror(smithingInput.template()))
            && base.test(CraftItemStack.asCraftMirror(smithingInput.base()))
            && RecipeUtils.testOptionalChoice(addition, CraftItemStack.asCraftMirror(smithingInput.addition()));
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.ItemStack result = CraftItemStack.asCraftMirror(this.result);
        return new CraftSmithingTransformRecipe(id, result, template.orElse(null), base, addition.orElse(null));
    }

    public static RecipeHolder<SmithingTransformRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTransformRecipe bukkitRecipe) {
        CraftSmithingTransformRecipe craftRecipe = CraftSmithingTransformRecipe.fromBukkitRecipe(bukkitRecipe);
        ItemStack nmsResult = CraftItemStack.asNMSCopy(bukkitRecipe.getResult());
        ItemStackTemplate resultTemplate = ItemStackTemplate.fromNonEmptyStack(nmsResult);
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTransformRecipe260100(commonInfo, craftRecipe.toNMSOptional(RecipeUtils.getBukkitChoice(bukkitRecipe.getTemplate()), false), Optional.ofNullable(bukkitRecipe.getTemplate()), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(bukkitRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMSOptional(RecipeUtils.getBukkitChoice(bukkitRecipe.getAddition()), false), Optional.ofNullable(bukkitRecipe.getAddition()), resultTemplate));
    }
}
