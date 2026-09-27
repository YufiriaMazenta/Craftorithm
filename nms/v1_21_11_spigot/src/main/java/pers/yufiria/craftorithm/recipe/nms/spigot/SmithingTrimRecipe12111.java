package pers.yufiria.craftorithm.recipe.nms.spigot;

import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.World;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftSmithingTrimRecipe;
import org.bukkit.craftbukkit.v1_21_R7.inventory.trim.CraftTrimPattern;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import pers.yufiria.craftorithm.recipe.nms.common.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.common.NmsSmithingRecipe;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe12111 extends SmithingTrimRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final Holder<TrimPattern> trimPattern;
    private PlacementInfo placementInfo;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTrimRecipe12111(NamespacedKey recipeKey, RecipeItemStack nmsTemplate, RecipeChoice template, RecipeItemStack nmsBase, RecipeChoice base, RecipeItemStack nmsAddition, RecipeChoice addition, Holder<TrimPattern> trimPattern) {
        super(nmsTemplate, nmsBase, nmsAddition, trimPattern);
        this.recipeKey = recipeKey;
        this.template = template;
        this.addition = addition;
        this.base = base;
        this.trimPattern = trimPattern;
    }

    @Override
    public NamespacedKey recipeKey() {
        return recipeKey;
    }

    @Override
    public Optional<RecipeChoice> templateChoice() {
        return Optional.ofNullable(template);
    }

    @Override
    public Optional<RecipeChoice> baseChoice() {
        return Optional.ofNullable(base);
    }

    @Override
    public Optional<RecipeChoice> additionChoice() {
        return Optional.ofNullable(addition);
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
    public PlacementInfo aq_() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.a(List.of(Optional.empty()));
        }
        return placementInfo;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> {
            Recipe recipe = new CraftSmithingTrimRecipe(id, template, base, addition, CraftTrimPattern.minecraftHolderToBukkit(this.trimPattern));
            return recipe;
        });
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTrimRecipe12111(recipeKey, craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getTemplate()), false), bukkitRecipe.getTemplate(), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getAddition()), false), bukkitRecipe.getAddition(), CraftTrimPattern.bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimPattern.BOLT)));
    }
}


