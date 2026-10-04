package pers.yufiria.craftorithm.recipe.nms.paper;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum CraftorithmRecipeRegistry260200 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        RecipeHolder<?> recipeHolder;
        if (!(bukkitRecipe instanceof TransmuteRecipe transmuteRecipe)) {
            return CraftorithmRecipeRegistry260100.INSTANCE.registerRecipe(bukkitRecipe, updateRecipes);
        }
        CraftTransmuteRecipe craftTransmuteRecipe = CraftTransmuteRecipe.fromBukkitRecipe(transmuteRecipe);
        recipeHolder = new RecipeHolder<>(
            CraftNamespacedKey.toResourceKey(Registries.RECIPE, transmuteRecipe.getKey()),
            new net.minecraft.world.item.crafting.TransmuteRecipe(
                new net.minecraft.world.item.crafting.Recipe.CommonInfo(true),
                new CraftingRecipe.CraftingBookInfo(
                    CraftRecipe.getCategory(craftTransmuteRecipe.getCategory()),
                    craftTransmuteRecipe.getGroup()
                ),
                CraftRecipe.toIngredient(craftTransmuteRecipe.getInput(), true),
                CraftRecipe.toIngredient(craftTransmuteRecipe.getMaterial(), true),
                net.minecraft.world.item.crafting.TransmuteRecipe.DEFAULT_MATERIAL_COUNT,
                CraftItemStack.asTemplate(craftTransmuteRecipe.getResult()),
                false
            )
        );
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
        return CraftorithmRecipeRegistry260100.INSTANCE.unregisterRecipe(recipeKey, updateRecipes);
    }

    @Override
    public void updateRecipes() {
        CraftorithmRecipeRegistry260100.INSTANCE.updateRecipes();
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        if (CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V26_2.name(), () -> this);
        }
    }
}
