package pers.yufiria.craftorithm.recipe.nms.paper;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.sync.RecipeSyncManager;
import pers.yufiria.craftorithm.util.IngredientUtils;

import java.util.Collection;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum CraftorithmRecipeRegistry12110 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean sync2Client) {
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
                        craftStonecuttingRecipe.toNMS(IngredientUtils.getBukkitChoice(stonecuttingRecipe.getInputChoice()), true),
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
        AsyncCatcher.catchOp("Recipe Add");
        MinecraftServer.getServer().getRecipeManager().recipes.addRecipe(recipeHolder);
        if (sync2Client) {
            RecipeSyncManager.INSTANCE.onRecipesChanged();
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> minecraftKey = CraftRecipe.toMinecraft(recipeKey);
        RecipeMap recipeMap = MinecraftServer.getServer().getRecipeManager().recipes;
        RecipeHolder<?> removed = recipeMap.byKey.remove(minecraftKey);
        if (removed == null) {
            return false;
        }
        Collection<? extends RecipeHolder<? extends net.minecraft.world.item.crafting.Recipe<?>>> recipes = recipeMap.byType(removed.value().getType());
        boolean result = recipes.remove(removed);
        if (result && sync2Client) {
            RecipeSyncManager.INSTANCE.onRecipesChanged();
        }
        return result;
    }

    @Override
    public void rebuildRecipes() {
        MinecraftServer server = MinecraftServer.getServer();
        server.getRecipeManager().finalizeRecipeLoading(server.getWorldData().enabledFeatures());
    }

    @Override
    public void buildRecipePacket() {
        MinecraftServer server = MinecraftServer.getServer();
        RecipeSyncManager.INSTANCE.setRecipePacket(new ClientboundUpdateRecipesPacket(
            server.getRecipeManager().getSynchronizedItemProperties(),
            server.getRecipeManager().getSynchronizedStonecutterRecipes()
        ));
    }

    @Override
    public boolean syncRecipes(Player player) {
        Object packet = RecipeSyncManager.INSTANCE.recipePacket();
        if (!(packet instanceof ClientboundUpdateRecipesPacket recipePacket) || !(player instanceof CraftPlayer craftPlayer)) {
            return false;
        }
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.connection.send(recipePacket);
        serverPlayer.getRecipeBook().sendInitialRecipeBook(serverPlayer);
        return true;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        //这个版本兼容1.21.9-1.21.11
        if (CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V1_21_9.name(), () -> this);
        }
    }
}
