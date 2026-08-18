package pers.yufiria.craftorithm.hook.protocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.temporary.TemporaryPlayer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import crypticlib.util.ItemHelper;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.fakeresult.FakeResultDataHandler;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.anvil.AnvilRecipeHandler;
import pers.yufiria.craftorithm.util.EventUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FakeResultPreviewPacketListener extends PacketAdapter implements Listener {

    public static final FakeResultPreviewPacketListener INSTANCE = new FakeResultPreviewPacketListener();

    private final Cache<UUID, CacheRecipeData> PLAYER_PREPARING_RECIPE = CacheBuilder
        .newBuilder()
        .expireAfterWrite(Duration.of(30, ChronoUnit.MINUTES))
        .build();

    private FakeResultPreviewPacketListener() {
        super(Craftorithm.instance(), PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void refreshCraftingRecipeCache(PrepareItemCraftEvent event) {
        UUID playerId = EventUtils.getViewer(event).map(HumanEntity::getUniqueId).orElse(null);
        if (playerId == null) return;
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
        UUID playerId = EventUtils.getViewer(event).map(HumanEntity::getUniqueId).orElse(null);
        if (playerId == null) return;
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
        UUID playerId = EventUtils.getViewer(event).map(HumanEntity::getUniqueId).orElse(null);
        if (playerId == null) return;
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
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (player instanceof TemporaryPlayer) {
            return;
        }
        UUID uuid = player.getUniqueId();
        CacheRecipeData cacheRecipeData = PLAYER_PREPARING_RECIPE.getIfPresent(uuid);
        if (cacheRecipeData == null) {
            return;
        }
        NamespacedKey recipeKey = cacheRecipeData.recipeKey;

        PacketType packetType = event.getPacketType();
        Optional<ItemStack> fakeResultOpt = FakeResultDataHandler.INSTANCE.getRecipeFakeResult(recipeKey);
        fakeResultOpt.ifPresent(fakeResult -> {
            if (packetType.equals(PacketType.Play.Server.SET_SLOT)) {
                int slot = event.getPacket().getIntegers().read(2);
                if (slot != cacheRecipeData.resultSlot) {
                    return;
                }
                event.getPacket().getItemModifier().write(0, fakeResult);
            } else if (packetType.equals(PacketType.Play.Server.WINDOW_ITEMS)) {
                List<ItemStack> items = event.getPacket().getItemListModifier().read(0);
                if (items != null && !items.isEmpty()) {
                    items.set(cacheRecipeData.resultSlot, fakeResult);
                    event.getPacket().getItemListModifier().write(0, items);
                }
            }
        });

    }

    private record CacheRecipeData(
        @NotNull NamespacedKey recipeKey,
        int resultSlot
    ) {}

}
