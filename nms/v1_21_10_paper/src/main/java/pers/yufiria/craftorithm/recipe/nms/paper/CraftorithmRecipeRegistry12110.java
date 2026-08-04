package pers.yufiria.craftorithm.recipe.nms.paper;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

import java.util.Collection;

@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.LOAD)
})
public enum CraftorithmRecipeRegistry12110 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
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
                CraftStonecuttingRecipe craftStonecuttingRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(stonecuttingRecipe);
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(craftStonecuttingRecipe.getKey()),
                    new StonecutterRecipe(
                        craftStonecuttingRecipe.getGroup(),
                        craftStonecuttingRecipe.toNMS(RecipeUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()), true),
                        CraftItemStack.asNMSCopy(craftStonecuttingRecipe.getResult())
                    )
                );
            }
            case TransmuteRecipe transmuteRecipe -> {
                CraftTransmuteRecipe craftTransmuteRecipe = CraftTransmuteRecipe.fromBukkitRecipe(transmuteRecipe);
                ItemStack unwrappedInternalStack = CraftItemStack.unwrap(craftTransmuteRecipe.getResult());
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(craftTransmuteRecipe.getKey()),
                    new net.minecraft.world.item.crafting.TransmuteRecipe(
                        craftTransmuteRecipe.getGroup(),
                        CraftRecipe.getCategory(craftTransmuteRecipe.getCategory()),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getInput(), true),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getMaterial(), true),
                        new TransmuteResult(unwrappedInternalStack.getItemHolder(), unwrappedInternalStack.getCount(), unwrappedInternalStack.getComponentsPatch())
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
            MinecraftServer.getServer().getRecipeManager().addRecipe(recipeHolder);
        } else {
            AsyncCatcher.catchOp("Recipe Add");
            MinecraftServer.getServer().getRecipeManager().recipes.addRecipe(recipeHolder);
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean updateRecipes) {
        if (updateRecipes) {
            return CraftorithmRecipeRegistry.super.unregisterRecipe(recipeKey, true);
        } else {
            ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> minecraftKey = CraftRecipe.toMinecraft(recipeKey);
            RecipeMap recipeMap = MinecraftServer.getServer().getRecipeManager().recipes;
            RecipeHolder<?> removed = recipeMap.byKey.remove(minecraftKey);
            if (removed == null) {
                return false;
            }
            Collection<? extends RecipeHolder<? extends net.minecraft.world.item.crafting.Recipe<?>>> recipes = recipeMap.byType(removed.value().getType());
            return recipes.remove(removed);
        }
    }

    @Override
    public void updateRecipes() {
        MinecraftServer.getServer().getRecipeManager().finalizeRecipeLoading();
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        //这个版本兼容1.21.9-1.21.11
        if (CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V1_21_9.name(), () -> this);
        }
    }
}
