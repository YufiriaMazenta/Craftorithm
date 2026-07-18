package pers.yufiria.craftorithm.recipe.nms.paper;

import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.*;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.TransmuteRecipe;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegister;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegister12110 implements CraftorithmRecipeRegister, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<?> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe12110.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe12110.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = SmeltingRecipe12110.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe12110.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireCookingRecipe12110.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe12110.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe12110.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe12110.fromBukkit(recipeKey, smithingTrimRecipe);
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
        MinecraftServer.getServer().getRecipeManager().addRecipe(recipeHolder);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        //这个版本兼容1.21.9-1.21.11
        if (CrypticLibBukkit.isPaper()) {
            REGISTER_COMPAT.register(MinecraftVersion.V1_21_9.name(), () -> this);
        }
    }
}
