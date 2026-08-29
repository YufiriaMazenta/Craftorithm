package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftSmithingTrimRecipe;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe260100 extends SmithingTrimRecipe {

    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final Holder<TrimPattern> trimPattern;
    private PlacementInfo placementInfo;
    private volatile org.bukkit.inventory.Recipe cachedBukkitRecipe;

    SmithingTrimRecipe260100(Recipe.CommonInfo commonInfo, Ingredient nmsTemplate, RecipeChoice template, Ingredient nmsBase, RecipeChoice base, Ingredient nmsAddition, RecipeChoice addition, Holder<TrimPattern> trimPattern) {
        super(commonInfo, nmsTemplate, nmsBase, nmsAddition, trimPattern);
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.trimPattern = trimPattern;
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
        return template.test(CraftItemStack.asCraftMirror(smithingInput.template()))
            && base.test(CraftItemStack.asCraftMirror(smithingInput.base()))
            && addition.test(CraftItemStack.asCraftMirror(smithingInput.addition()));
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe(NamespacedKey id) {
        org.bukkit.inventory.Recipe cached = cachedBukkitRecipe;
        if (cached != null) {
            return cached;
        }
        org.bukkit.inventory.Recipe recipe = new CraftSmithingTrimRecipe(id, template, base, addition, CraftTrimPattern.minecraftHolderToBukkit(this.trimPattern));
        cachedBukkitRecipe = recipe;
        return recipe;
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        Recipe.CommonInfo commonInfo = new Recipe.CommonInfo(true);
        return new RecipeHolder<>(
            CraftRecipe.toMinecraft(recipeKey),
            new SmithingTrimRecipe260100(
                commonInfo,
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getTemplate()), false),
                bukkitRecipe.getTemplate(),
                craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getBase()), false),
                bukkitRecipe.getBase(), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getAddition()), false),
                bukkitRecipe.getAddition(),
                CraftTrimPattern.bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimPattern.BOLT)
            )
        );
    }
}
