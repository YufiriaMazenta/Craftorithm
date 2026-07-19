package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R1.inventory.CraftRecipe;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegistry12100 implements CraftorithmRecipeRegistry, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe12100.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe12100.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = FurnaceRecipe12100.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe12100.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireRecipe12100.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe12100.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe12100.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe12100.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                recipeHolder = StonecuttingRecipe12100.fromBukkit(recipeKey, stonecuttingRecipe);
            }
            case CraftRecipe craftRecipe -> {
                craftRecipe.addToCraftingManager();
                return RegisterResult.SUCCESS;
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aJ().addRecipe(recipeHolder);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        REGISTRY_COMPAT.register(
            MinecraftVersion.V1_21.name(),
            () -> this
        );
    }

}
