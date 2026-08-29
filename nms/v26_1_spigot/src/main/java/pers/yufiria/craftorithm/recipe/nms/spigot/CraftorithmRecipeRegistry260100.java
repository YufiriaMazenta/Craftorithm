package pers.yufiria.craftorithm.recipe.nms.spigot;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.IngredientUtils;

@LifecycleTaskSettings(rules = {
    @LifecycleRule(lifeCycle = Lifecycle.LOAD)
})
public enum CraftorithmRecipeRegistry260100 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean updateRecipes) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<?> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe260100.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe260100.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = SmeltingRecipe260100.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe260100.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireCookingRecipe260100.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe260100.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe260100.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe260100.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                CraftStonecuttingRecipe craftStonecuttingRecipe = CraftStonecuttingRecipe.fromBukkitRecipe(stonecuttingRecipe);
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(recipeKey),
                    new StonecutterRecipe(
                        new net.minecraft.world.item.crafting.Recipe.CommonInfo(true),
                        craftStonecuttingRecipe.toNMS(IngredientUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()), true),
                        CraftItemStack.asNMSTemplate(craftStonecuttingRecipe.getResult())
                    )
                );
            }
            case TransmuteRecipe transmuteRecipe -> {
                CraftTransmuteRecipe craftTransmuteRecipe = CraftTransmuteRecipe.fromBukkitRecipe(transmuteRecipe);
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(recipeKey),
                    new net.minecraft.world.item.crafting.TransmuteRecipe(
                        new net.minecraft.world.item.crafting.Recipe.CommonInfo(true),
                        new CraftingRecipe.CraftingBookInfo(
                            CraftRecipe.getCategory(craftTransmuteRecipe.getCategory()),
                            craftTransmuteRecipe.getGroup()
                        ),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getInput(), true),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getMaterial(), true),
                        net.minecraft.world.item.crafting.TransmuteRecipe.DEFAULT_MATERIAL_COUNT,
                        CraftItemStack.asNMSTemplate(craftTransmuteRecipe.getResult()),
                        false
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
            return MinecraftServer.getServer().getRecipeManager().recipes.removeRecipe(CraftRecipe.toMinecraft(recipeKey));
        }
    }

    @Override
    public void updateRecipes() {
        MinecraftServer.getServer().getRecipeManager().finalizeRecipeLoading();
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        if (!CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V26_1.name(), () -> this);
        }
    }
}
