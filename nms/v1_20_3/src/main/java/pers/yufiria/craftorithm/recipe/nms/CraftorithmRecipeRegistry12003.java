package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftRecipe;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum CraftorithmRecipeRegistry12003 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
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
            case CraftRecipe craftRecipe -> {
                craftRecipe.addToCraftingManager();
                return RegisterResult.SUCCESS;
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aG().addRecipe(nms);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        REGISTRY_COMPAT.register(MinecraftVersion.V1_20_3.name(), () -> this);
    }
}
