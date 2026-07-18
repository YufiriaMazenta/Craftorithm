package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.IRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegistry12000 implements CraftorithmRecipeRegistry, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        IRecipe<?> nms;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                nms = ShapedRecipe12000.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                nms = ShapelessRecipe12000.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                nms = FurnaceRecipe12000.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                nms = SmokingRecipe12000.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                nms = CampfireRecipe12000.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                nms = BlastingRecipe12000.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                nms = SmithingTransformRecipe12000.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                nms = SmithingTrimRecipe12000.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                nms = StonecuttingRecipe12000.fromBukkit(recipeKey, stonecuttingRecipe);
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aE().addRecipe(nms);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object o, LifeCycle lifeCycle) {
        REGISTRY_COMPAT.register(MinecraftVersion.V1_20.name(), () -> this);
    }
}
