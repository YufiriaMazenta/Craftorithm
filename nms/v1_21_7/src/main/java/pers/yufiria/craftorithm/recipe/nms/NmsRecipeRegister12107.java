package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.register.BukkitRecipeRegister;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.LOAD)
    }
)
public enum NmsRecipeRegister12107 implements NmsRecipeRegister, LifeCycleTask {

    INSTANCE;

    @Override
    public boolean registerRecipe(Recipe bukkitRecipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        IOHelper.info(bukkitRecipe.getClass().toString());
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                RecipeHolder<ShapedRecipes> recipeHolder = ShapedRecipe12107.fromBukkit(recipeKey, shapedRecipe);
                MinecraftServer.getServer().aI().addRecipe(recipeHolder);
                return true;
            }
            case ShapelessRecipe shapelessRecipe -> {
                RecipeHolder<ShapelessRecipes> recipeHolder = ShapelessRecipe12107.fromBukkit(recipeKey, shapelessRecipe);
                MinecraftServer.getServer().aI().addRecipe(recipeHolder);
                return true;
            }
            default -> {
                //TODO
                return false;
            }
        }
    }

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        BukkitRecipeRegister.INSTANCE.nmsRegisterCompat().register(
            MinecraftVersion.V1_21_7.name(),
            () -> this
        );
    }

}
