package pers.yufiria.craftorithm.recipe.nms;

import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import net.minecraft.network.protocol.game.PacketPlayOutRecipeUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeStonecutting;
import net.minecraft.world.item.crafting.TransmuteResult;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftRecipe;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftStonecuttingRecipe;
import org.bukkit.craftbukkit.v1_21_R4.inventory.CraftTransmuteRecipe;
import org.bukkit.inventory.*;
import org.spigotmc.AsyncCatcher;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.sync.RecipeSyncManager;
import pers.yufiria.craftorithm.util.IngredientUtils;

@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.LOAD))
public enum NmsRecipeAccessor12105 implements NmsRecipeAccessor, LifecycleTask {

    INSTANCE;

    @Override
    public RegisterResult registerRecipe(Recipe bukkitRecipe, boolean sync2Client) {
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(bukkitRecipe);
        RecipeHolder<? extends IRecipe<?>> recipeHolder;
        switch (bukkitRecipe) {
            case ShapedRecipe shapedRecipe -> {
                recipeHolder = ShapedRecipe12105.fromBukkit(recipeKey, shapedRecipe);
            }
            case ShapelessRecipe shapelessRecipe -> {
                recipeHolder = ShapelessRecipe12105.fromBukkit(recipeKey, shapelessRecipe);
            }
            case FurnaceRecipe furnaceRecipe -> {
                recipeHolder = FurnaceRecipe12105.fromBukkit(recipeKey, furnaceRecipe);
            }
            case SmokingRecipe smokingRecipe -> {
                recipeHolder = SmokingRecipe12105.fromBukkit(recipeKey, smokingRecipe);
            }
            case CampfireRecipe campfireRecipe -> {
                recipeHolder = CampfireRecipe12105.fromBukkit(recipeKey, campfireRecipe);
            }
            case BlastingRecipe blastingRecipe -> {
                recipeHolder = BlastingRecipe12105.fromBukkit(recipeKey, blastingRecipe);
            }
            case SmithingTransformRecipe smithingTransformRecipe -> {
                recipeHolder = SmithingTransformRecipe12105.fromBukkit(recipeKey, smithingTransformRecipe);
            }
            case SmithingTrimRecipe smithingTrimRecipe -> {
                recipeHolder = SmithingTrimRecipe12105.fromBukkit(recipeKey, smithingTrimRecipe);
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
                //paper的CraftTransmuteRecipe没有toNMS(ItemStack), 直接由nms物品构建结果
                net.minecraft.world.item.ItemStack nmsResult = CraftItemStack.asNMSCopy(craftTransmuteRecipe.getResult());
                recipeHolder = new RecipeHolder<>(
                    CraftRecipe.toMinecraft(craftTransmuteRecipe.getKey()),
                    new net.minecraft.world.item.crafting.TransmuteRecipe(
                        craftTransmuteRecipe.getGroup(),
                        CraftRecipe.getCategory(craftTransmuteRecipe.getCategory()),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getInput(), true),
                        craftTransmuteRecipe.toNMS(craftTransmuteRecipe.getMaterial(), true),
                        new TransmuteResult(nmsResult.i(), nmsResult.M(), nmsResult.d())
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
        MinecraftServer.getServer().aI().e.addRecipe(recipeHolder);
        if (sync2Client) {
            RecipeSyncManager.INSTANCE.onRecipesChanged();
        }
        return RegisterResult.SUCCESS;
    }

    @Override
    public boolean unregisterRecipe(NamespacedKey recipeKey, boolean sync2Client) {
        boolean removed = MinecraftServer.getServer().aI().e.removeRecipe(CraftRecipe.toMinecraft(recipeKey));
        if (removed && sync2Client) {
            RecipeSyncManager.INSTANCE.onRecipesChanged();
        }
        return removed;
    }

    @Override
    public void rebuildRecipes() {
        MinecraftServer server = MinecraftServer.getServer();
        server.aI().a(server.aZ().K());
    }

    @Override
    public void buildRecipePacket() {
        MinecraftServer server = MinecraftServer.getServer();
        RecipeSyncManager.INSTANCE.setRecipePacket(new PacketPlayOutRecipeUpdate(server.aI().b(), server.aI().c()));
    }

    @Override
    public boolean syncRecipes(Player player) {
        Object packet = RecipeSyncManager.INSTANCE.recipePacket();
        if (!(packet instanceof PacketPlayOutRecipeUpdate recipePacket) || !(player instanceof CraftPlayer craftPlayer)) {
            return false;
        }
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        entityPlayer.f.b(recipePacket);
        entityPlayer.I().a(entityPlayer);
        return true;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        COMPAT.register(MinecraftVersion.V1_21_5.name(), () -> this);
    }
}

