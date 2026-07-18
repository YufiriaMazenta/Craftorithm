package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTask;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftStonecuttingRecipe;
import org.bukkit.inventory.*;
import pers.yufiria.craftorithm.api.recipe.CraftorithmRecipeRegister;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.RecipeUtils;

@LifeCycleTaskSettings(rules = {
    @TaskRule(lifeCycle = LifeCycle.LOAD)
})
public enum CraftorithmRecipeRegister12005 implements CraftorithmRecipeRegister, LifeCycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> nms;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                nms = ShapedRecipe12005.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                nms = ShapelessRecipe12005.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                nms = FurnaceRecipe12005.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                nms = SmokingRecipe12005.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                nms = CampfireRecipe12005.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                nms = BlastingRecipe12005.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                nms = SmithingTransformRecipe12005.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                nms = SmithingTrimRecipe12005.fromBukkit(recipeKey, smithingTrimRecipe);
            }
            case StonecuttingRecipe stonecuttingRecipe -> {
                //切石机配方自定义匹配逻辑存在问题，暂时还是按照原版的来
                stonecuttingRecipe.setInputChoice(RecipeUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()));
                CraftStonecuttingRecipe.fromBukkitRecipe(stonecuttingRecipe).addToCraftingManager();
                return RegisterResult.SUCCESS;
            }
            default -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
        }
        MinecraftServer.getServer().aJ().addRecipe(nms);
        return RegisterResult.SUCCESS;
    }

    @Override
    public void lifecycle(Object o, LifeCycle lifeCycle) {
        REGISTER_COMPAT.register(MinecraftVersion.V1_20_5.name(), () -> this);
    }
}
