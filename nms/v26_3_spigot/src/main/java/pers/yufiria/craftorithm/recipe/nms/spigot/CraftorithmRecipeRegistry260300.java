package pers.yufiria.craftorithm.recipe.nms.spigot;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.brewing.BrewingRecipe;

/**
 * 对26.3的酿造配方进行的额外适配
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum CraftorithmRecipeRegistry260300 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        if (bukkitRecipe instanceof BrewingRecipe brewingRecipe) {
            NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
            RecipeHolder<?> recipeHolder;
            recipeHolder = BrewingRecipe260300.fromBukkit(recipeKey, brewingRecipe);
            if (updateRecipes) {
                MinecraftServer.getServer().getRecipeManager().addRecipe(recipeHolder);
            } else {
                AsyncCatcher.catchOp("Recipe Add");
                MinecraftServer.getServer().getRecipeManager().recipes.addRecipe(recipeHolder);
            }
        } else {
            return CraftorithmRecipeRegistry260100.INSTANCE.registerRecipe(bukkitRecipe, updateRecipes);
        }

        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        return CraftorithmRecipeRegistry260100.INSTANCE.unregisterRecipe(recipeKey, updateRecipes);
    }

    @Override
    public void updateRecipes() {
        MinecraftServer.getServer().getRecipeManager().finalizeRecipeLoading();
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        if (!CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V26_3.name(), () -> this);
        }
    }
}
