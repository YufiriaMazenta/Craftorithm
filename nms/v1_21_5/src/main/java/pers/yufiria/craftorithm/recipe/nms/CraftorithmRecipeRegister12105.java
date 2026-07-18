package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.*;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegister;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegister12105 implements CraftorithmRecipeRegister, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe12105.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe12105.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = FurnaceRecipe12105.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe12105.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireRecipe12105.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe12105.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe12105.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe12105.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                stonecuttingRecipe.setInputChoice(RecipeUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()));
                CraftStonecuttingRecipe.fromBukkitRecipe(stonecuttingRecipe).addToCraftingManager();
                return RegisterResult.SUCCESS;
            }
            case TransmuteRecipe transmuteRecipe -> {
                Bukkit.addRecipe(transmuteRecipe);
                return RegisterResult.SUCCESS;
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aI().addRecipe(recipeHolder);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        REGISTER_COMPAT.register(MinecraftVersion.V1_21_5.name(), () -> this);
    }
}

