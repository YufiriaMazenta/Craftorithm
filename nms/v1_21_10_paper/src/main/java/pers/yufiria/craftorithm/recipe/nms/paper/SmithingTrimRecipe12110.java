package pers.yufiria.craftorithm.recipe.nms.paper;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmithingTrimRecipe;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe12110 extends SmithingTrimRecipe {

    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final Holder<TrimPattern> trimPattern;
    private PlacementInfo placementInfo;

    SmithingTrimRecipe12110(Ingredient nmsTemplate, RecipeChoice template, Ingredient nmsBase, RecipeChoice base, Ingredient nmsAddition, RecipeChoice addition, Holder<TrimPattern> trimPattern) {
        super(nmsTemplate, nmsBase, nmsAddition, trimPattern);
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.trimPattern = trimPattern;
    }

    @Override
    public boolean matches(SmithingRecipeInput smithingInput, Level level) {
        return template.test(CraftItemStack.asCraftMirror(smithingInput.template()))
            && base.test(CraftItemStack.asCraftMirror(smithingInput.base()))
            && addition.test(CraftItemStack.asCraftMirror(smithingInput.addition()));
    }

    @Override
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.empty()));
        }
        return super.placementInfo();
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftSmithingTrimRecipe(id, template, base, addition, CraftTrimPattern.minecraftHolderToBukkit(this.trimPattern));
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTrimRecipe12110(craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getTemplate()), false), bukkitRecipe.getTemplate(), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getAddition()), false), bukkitRecipe.getAddition(), CraftTrimPattern.bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimPattern.BOLT)));
    }
}
