package pers.yufiria.craftorithm.recipe.nms.spigot;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_21_R7.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.IngredientUtils;

@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.LOAD)
})
public enum CraftorithmRecipeRegistry12111 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe12111.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe12111.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = FurnaceRecipe12111.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe12111.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireRecipe12111.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe12111.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe12111.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe12111.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                CraftStonecuttingRecipe craftStonecuttingRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(stonecuttingRecipe);
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(craftStonecuttingRecipe.getKey()),
                    new RecipeStonecutting(
                        craftStonecuttingRecipe.getGroup(),
                        craftStonecuttingRecipe.toNMS(IngredientUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()), true),
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
            MinecraftServer.getServer().aJ().addRecipe(recipeHolder);
        } else {
            AsyncCatcher.catchOp("Recipe Add");
            MinecraftServer.getServer().aJ().e.addRecipe(recipeHolder);
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        if (updateRecipes) {
            return CraftorithmRecipeRegistry.super.unregisterRecipe(recipeKey, true);
        } else {
            return MinecraftServer.getServer().aJ().e.removeRecipe(CraftRecipe.toMinecraft(recipeKey));
        }
    }

    @Override
    public void updateRecipes() {
        MinecraftServer.getServer().aJ().finalizeRecipeLoading();
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        if (!CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V1_21_11.name(), () -> this);
        }
    }
}


