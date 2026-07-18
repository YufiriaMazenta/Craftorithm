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
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegister;
import pers.yufiria.craftorithm.recipe.RecipeManager;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegister12003 implements CraftorithmRecipeRegister, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> nms;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                nms = ShapedRecipe12003.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                nms = ShapelessRecipe12003.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                nms = FurnaceRecipe12003.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                nms = SmokingRecipe12003.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                nms = CampfireRecipe12003.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                nms = BlastingRecipe12003.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                nms = SmithingTransformRecipe12003.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                nms = SmithingTrimRecipe12003.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                nms = StonecuttingRecipe12003.fromBukkit(recipeKey, stonecuttingRecipe);
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aG().addRecipe(nms);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object o, LifeCycle lifeCycle) {
        REGISTER_COMPAT.register(MinecraftVersion.V1_20_3.name(), () -> this);
    }
}
