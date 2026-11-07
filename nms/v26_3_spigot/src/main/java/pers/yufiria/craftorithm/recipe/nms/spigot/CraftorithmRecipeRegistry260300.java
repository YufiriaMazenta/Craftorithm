package pers.yufiria.craftorithm.recipe.nms.spigot;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.BrewingRecipe;
import pers.yufiria.craftorithm.recipe.CraftorithmRecipeRegistry;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeSyncManager;

/**
 * 对26.3的酿造配方进行的额外适配
 */
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum CraftorithmRecipeRegistry260300 implements CraftorithmRecipeRegistry, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean sync2Client) {
        switch (bukkitRecipe) {
            case BrewingRecipe brewingRecipe -> {
                NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
                RecipeHolder<?> recipeHolder;
                recipeHolder = BrewingRecipe260300.fromBukkit(recipeKey, brewingRecipe);
                AsyncCatcher.catchOp("Recipe Add");
                MinecraftServer.getServer().getRecipeManager().recipes.addRecipe(recipeHolder);
                if (sync2Client) {
                    RecipeSyncManager.INSTANCE.onRecipesChanged();
                }
            }
            case org.bukkit.inventory.BrewingRecipe ignored -> {
                return RegisterResult.UNSUPPORTED_RECIPE_TYPE;
            }
            default -> {
                return CraftorithmRecipeRegistry260100.INSTANCE.registerRecipe(bukkitRecipe, sync2Client);
            }
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        return CraftorithmRecipeRegistry260100.INSTANCE.unregisterRecipe(recipeKey, sync2Client);
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
        if (!CrypticLibBukkit.isPaper()) {
            REGISTRY_COMPAT.register(MinecraftVersion.V26_3.name(), () -> this);
        }
    }
}
