package pers.yufiria.craftorithm.hook.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import crypticlib.util.ItemHelper;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.craftorithm.fakeResult.FakeResultDataHandler;
import pers.yufiria.craftorithm.item.ItemManager;
import pers.yufiria.craftorithm.item.NamespacedItemIdStack;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.extra.AnvilRecipeHandler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public enum FakeResultPreviewPacketListener implements PacketListener, Listener {

    INSTANCE;

    private final Cache<UUID, CacheRecipeData> PLAYER_PREPARING_RECIPE = CacheBuilder
        .newBuilder()
        .expireAfterWrite(Duration.of(30, ChronoUnit.MINUTES))
        .build();

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshCraftingRecipeCache(PrepareItemCraftEvent event) {
        UUID playerId = event.getViewers().getFirst().getUniqueId();
        if (ItemHelper.isAir(event.getInventory().getResult())) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        PLAYER_PREPARING_RECIPE.put(playerId, new CacheRecipeData(
            recipeKey,
            0
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshSmithingRecipeCache(PrepareSmithingEvent event) {
        UUID playerId = event.getViewers().getFirst().getUniqueId();
        if (ItemHelper.isAir(event.getInventory().getResult())) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        Recipe recipe = event.getInventory().getRecipe();
        if (recipe == null) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        PLAYER_PREPARING_RECIPE.put(playerId, new CacheRecipeData(
            recipeKey,
            3
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshAnvilCache(PrepareAnvilEvent event) {
        UUID playerId = event.getViewers().getFirst().getUniqueId();
        if (ItemHelper.isAir(event.getInventory().getResult())) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        AnvilInventory anvilInventory = event.getInventory();
        Recipe recipe = AnvilRecipeHandler.INSTANCE.matchAnvilRecipe(anvilInventory.getItem(0), anvilInventory.getItem(1));
        if (recipe == null) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        NamespacedKey recipeKey = RecipeManager.INSTANCE.getRecipeKey(recipe);
        if (recipeKey == null) {
            //当玩家预览配方为null时，去除缓存
            PLAYER_PREPARING_RECIPE.invalidate(playerId);
            return;
        }
        PLAYER_PREPARING_RECIPE.put(playerId, new CacheRecipeData(
            recipeKey,
            2
        ));
    }

    @EventHandler
    public void onPlayerCloseInvClearCache(InventoryCloseEvent event) {
        PLAYER_PREPARING_RECIPE.invalidate(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuitClearCache(PlayerQuitEvent event) {
        PLAYER_PREPARING_RECIPE.invalidate(event.getPlayer().getUniqueId());
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        User user = event.getUser();
        UUID uuid = user.getUUID();
        if (uuid == null) {
            //这个地方千万不能去掉,不然启动时因为没有玩家会报错
            return;
        }

        CacheRecipeData cacheRecipeData = PLAYER_PREPARING_RECIPE.getIfPresent(uuid);
        if (cacheRecipeData == null) {
            return;
        }
        NamespacedKey recipeKey = cacheRecipeData.recipeKey;

        switch (event.getPacketType()) {
            case PacketType.Play.Server.SET_SLOT -> {
                NamespacedItemIdStack recipeFakeResult = FakeResultDataHandler.INSTANCE.getRecipeFakeResult(recipeKey);
                if (recipeFakeResult == null) {
                    return;
                }
                ItemStack fakeResult = ItemManager.INSTANCE.matchItem(recipeFakeResult);
                WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot(event);
                if (packet.getSlot() != cacheRecipeData.resultSlot) {
                    return;
                }
                packet.setItem(SpigotConversionUtil.fromBukkitItemStack(fakeResult));
            }
            case PacketType.Play.Server.WINDOW_ITEMS -> {
                NamespacedItemIdStack recipeFakeResult = FakeResultDataHandler.INSTANCE.getRecipeFakeResult(recipeKey);
                if (recipeFakeResult == null) {
                    return;
                }
                ItemStack fakeResult = ItemManager.INSTANCE.matchItem(recipeFakeResult);
                WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(event);
                List<com.github.retrooper.packetevents.protocol.item.ItemStack> items = packet.getItems();
                if (!items.isEmpty()) {
                    items.set(cacheRecipeData.resultSlot, SpigotConversionUtil.fromBukkitItemStack(fakeResult));
                    packet.setItems(items);
                }
            }
            default -> {
                return;
            }
        }
        event.markForReEncode(true);
    }

    private record CacheRecipeData(
        @NotNull NamespacedKey recipeKey,
        int resultSlot
    ) {}

}
