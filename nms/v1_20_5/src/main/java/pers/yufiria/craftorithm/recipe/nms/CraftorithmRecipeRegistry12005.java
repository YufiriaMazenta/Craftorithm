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
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftRecipe;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegistry12005 implements CraftorithmRecipeRegistry, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> nms;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                nms = ShapedRecipe12005.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                nms = ShapelessRecipe12005.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                nms = FurnaceRecipe12005.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                nms = SmokingRecipe12005.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                nms = CampfireRecipe12005.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                nms = BlastingRecipe12005.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                nms = SmithingTransformRecipe12005.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                nms = SmithingTrimRecipe12005.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                nms = StonecuttingRecipe12005.fromBukkit(recipeKey, stonecuttingRecipe);
            }
            case CraftRecipe craftRecipe -> {
                craftRecipe.addToCraftingManager();
                return RegisterResult.SUCCESS;
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aJ().addRecipe(nms);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object o, LifeCycle lifeCycle) {
        REGISTRY_COMPAT.register(MinecraftVersion.V1_20_5.name(), () -> this);
    }
}
