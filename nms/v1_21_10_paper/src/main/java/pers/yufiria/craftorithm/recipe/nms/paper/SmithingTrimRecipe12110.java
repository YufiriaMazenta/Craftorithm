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
import pers.yufiria.craftorithm.recipe.nms.CachedBukkitRecipe;
import pers.yufiria.craftorithm.recipe.nms.NmsSmithingRecipe;
import pers.yufiria.craftorithm.recipe.nms.input.BukkitSmithingInput;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipe12110 extends SmithingTrimRecipe implements NmsSmithingRecipe {

    private final NamespacedKey recipeKey;
    private final RecipeChoice template, addition;
    private final RecipeChoice base;
    private final Holder<TrimPattern> trimPattern;
    private PlacementInfo placementInfo;
    private final CachedBukkitRecipe cachedBukkitRecipe = new CachedBukkitRecipe();

    SmithingTrimRecipe12110(NamespacedKey recipeKey, Ingredient nmsTemplate, RecipeChoice template, Ingredient nmsBase, RecipeChoice base, Ingredient nmsAddition, RecipeChoice addition, Holder<TrimPattern> trimPattern) {
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
    public PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(List.of(Optional.empty()));
        }
        return placementInfo;
    }

    @Override
    public Recipe toBukkitRecipe(NamespacedKey id) {
        return cachedBukkitRecipe.get(() -> new CraftSmithingTrimRecipe(id, template, base, addition, CraftTrimPattern.minecraftHolderToBukkit(this.trimPattern)));
    }

    public static RecipeHolder<SmithingTrimRecipe> fromBukkit(NamespacedKey recipeKey, org.bukkit.inventory.SmithingTrimRecipe bukkitRecipe) {
        CraftSmithingTrimRecipe craftRecipe = CraftSmithingTrimRecipe.fromBukkitRecipe(bukkitRecipe);
        return new RecipeHolder<>(CraftRecipe.toMinecraft(recipeKey), new SmithingTrimRecipe12110(recipeKey, craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getTemplate()), false), bukkitRecipe.getTemplate(), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getBase()), false), bukkitRecipe.getBase(), craftRecipe.toNMS(IngredientUtils.getBukkitChoice(craftRecipe.getAddition()), false), bukkitRecipe.getAddition(), CraftTrimPattern.bukkitToMinecraftHolder(org.bukkit.inventory.meta.trim.TrimPattern.BOLT)));
    }
}
