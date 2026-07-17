package pers.yufiria.craftorithm.recipe.nms;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftSmithingTransformRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftSmithingTrimRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.trim.CraftTrimPattern;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe12107 extends SmithingTrimRecipe {

    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final Holder<TrimPattern> trimPattern;
    private PlacementInfo placementInfo;

    SmithingTrimRecipe12107(
        RecipeItemStack nmsTemplate,
        RecipeChoice template,
        RecipeItemStack nmsBase,
        RecipeChoice base,
        RecipeItemStack nmsAddition,
        RecipeChoice addition,
        Holder<TrimPattern> trimPattern
    ) {
        super(nmsTemplate, nmsBase, nmsAddition, trimPattern);
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.trimPattern = trimPattern;
    }

    @Override
    public boolean a(SmithingRecipeInput smithingInput, World world) {
        return template.test(CraftItemStack.asCraftMirror(smithingInput.c()))
            && base.test(CraftItemStack.asCraftMirror(smithingInput.d()))
            && addition.test(CraftItemStack.asCraftMirror(smithingInput.e()));
    }

    @Override
    public PlacementInfo ao_() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.a(List.of(Optional.empty()));
        }
        return placementInfo;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return new CraftSmithingTrimRecipe(id, template, base, addition, CraftTrimPattern.minecraftHolderToBukkit(this.trimPattern));
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(
            CraftRecipe.toMinecraft(recipeKey),
            new SmithingTrimRecipe12107(
                craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getTemplate()), false),
                bukkitRecipe.getTemplate(),
                craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getBase()), false),
                bukkitRecipe.getBase(),
                craftRecipe.toNMS(RecipeUtils.getBukkitChoice(craftRecipe.getAddition()), false),
                bukkitRecipe.getAddition(),
                CraftTrimPattern.bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimPattern.BOLT)
            )
        );
    }

}
