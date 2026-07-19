package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_21_R5.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.LOAD)
    }
)
public enum CraftorithmRecipeRegistry12107 implements CraftorithmRecipeRegistry, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe12107.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe12107.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = FurnaceRecipe12107.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe12107.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireRecipe12107.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe12107.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe12107.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe12107.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                CraftStonecuttingRecipe craftStonecuttingRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(stonecuttingRecipe);
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(craftStonecuttingRecipe.getKey()),
                    new RecipeStonecutting(
                        craftStonecuttingRecipe.getGroup(),
                        craftStonecuttingRecipe.toNMS(RecipeUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()), true),
                        CraftItemStack.asNMSCopy(craftStonecuttingRecipe.getResult())
                    )
                );
            }
            case TransmuteRecipe transmuteRecipe -> {
                CraftTransmuteRecipe craftTransmuteRecipe = CraftTransmuteRecipe.fromBukkitRecipe(transmuteRecipe);
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(craftTransmuteRecipe.getKey()),
                    new net.minecraft.world.item.crafting.TransmuteRecipe(
                        craftTransmuteRecipe.getGroup(),
                        CraftRecipe.getCategory(craftTransmuteRecipe.getCategory()),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getInput(), true),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getMaterial(), true),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getResult())
                    )
                );
            }
            case CraftRecipe craftRecipe -> {
                craftRecipe.addToCraftingManager();
                return RegisterResult.SUCCESS;
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        if (updateRecipes) {
            MinecraftServer.getServer().aI().addRecipe(recipeHolder);
        } else {
            AsyncCatcher.catchOp("Recipe Add");
            MinecraftServer.getServer().aI().e.addRecipe(recipeHolder);
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        if (updateRecipes) {
            return CraftorithmRecipeRegistry.super.unregisterRecipe(recipeKey, true);
        } else {
            return MinecraftServer.getServer().aI().e.removeRecipe(CraftRecipe.toMinecraft(recipeKey));
        }
    }

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        REGISTRY_COMPAT.register(
            MinecraftVersion.V1_21_7.name(),
            () -> this
        );
    }

}
