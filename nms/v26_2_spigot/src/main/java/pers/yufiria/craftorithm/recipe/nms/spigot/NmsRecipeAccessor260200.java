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
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftRecipe;
import org.bukkit.craftbukkit.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.TransmuteRecipe;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.nms.NmsRecipeAccessor;
import pers.yufiria.craftorithm.recipe.sync.RecipeSyncManager;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum NmsRecipeAccessor260200 implements NmsRecipeAccessor, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean sync2Client) {
        RecipeHolder<?> recipeHolder;
        if (!(bukkitRecipe instanceof TransmuteRecipe transmuteRecipe)) {
            return NmsRecipeAccessor260100.INSTANCE.registerRecipe(bukkitRecipe, sync2Client);
        }
        CraftTransmuteRecipe craftTransmuteRecipe = CraftTransmuteRecipe.fromBukkitRecipe(transmuteRecipe);
        recipeHolder = new RecipeHolder<>(
            CraftRecipe.toMinecraft(transmuteRecipe.getKey()),
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
        AsyncCatcher.catchOp("Recipe Add");
        MinecraftServer.getServer().getRecipeManager().recipes.addRecipe(recipeHolder);
        if (sync2Client) {
            RecipeSyncManager.INSTANCE.onRecipesChanged();
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        return NmsRecipeAccessor260100.INSTANCE.unregisterRecipe(recipeKey, sync2Client);
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
            COMPAT.register(MinecraftVersion.V26_2.name(), () -> this);
        }
    }
}
