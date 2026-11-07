package pers.yufiria.craftorithm.recipe;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.listener.EventListener;
import crypticlib.util.InventoryViewHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRecipeBookSettingsChangeEvent;
import pers.yufiria.craftorithm.config.PluginConfigs;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 配方同步调度: 配方变更后把配方下发给玩家
 * <p>
 * 两种方式: 一次性同步全部在线玩家(默认), 或者先标记, 等玩家打开配方界面时再同步
 */
@EventListener
@LifecycleTaskConfig(schedules = @LifecycleSchedule(phase = LifecyclePhase.DISABLE))
public enum RecipeSyncManager implements LifecycleTask, Listener {

    INSTANCE;

    private static final Set<InventoryType> RECIPE_MENU_TYPES = EnumSet.of(
        InventoryType.WORKBENCH,
        InventoryType.FURNACE,
        InventoryType.BLAST_FURNACE,
        InventoryType.SMOKER,
        InventoryType.STONECUTTER,
        InventoryType.SMITHING,
        InventoryType.ANVIL
    );

    private final Set<UUID> pendingPlayers = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean unsupportedWarned = new AtomicBoolean(false);
    //当前最新配方包, 具体类型由当前版本的 CraftorithmRecipeRegistry 决定
    private volatile Object recipePacket = null;

    /**
     * 保存当前最新配方包, 由 buildRecipePacket 调用
     */
    public void setRecipePacket(Object recipePacket) {
        this.recipePacket = recipePacket;
    }

    public Object recipePacket() {
        return recipePacket;
    }

    /**
     * 配方数据变更后的统一入口, 只能在主线程调用
     */
    public void onRecipesChanged() {
        CraftorithmRecipeRegistry registry = CraftorithmRecipeRegistry.findImpl();
        if (!PluginConfigs.LAZY_RECIPE_SYNC.value()) {
            registry.syncRecipes();
            return;
        }
        registry.rebuildRecipes();
        registry.buildRecipePacket();
        markAllPending();
        //重建后展示id会整体位移, 正在使用配方面板的玩家必须立即刷新
        syncPlayersWithRecipeMenu();
    }

    /**
     * 立即向单个玩家下发配方数据
     */
    public void syncPlayer(Player player) {
        pendingPlayers.remove(player.getUniqueId());
        if (CraftorithmRecipeRegistry.findImpl().syncRecipes(player)) {
            return;
        }
        //不支持逐玩家下发时退化为全量推送
        if (unsupportedWarned.compareAndSet(false, true)) {
            CrypticLib.warn("Per-player recipe sync is not supported on this server, fallback to full update");
            CraftorithmRecipeRegistry.findImpl().syncRecipes();
        }
    }

    /**
     * 仅在玩家待同步时下发
     */
    public void syncIfPending(Player player) {
        if (pendingPlayers.contains(player.getUniqueId())) {
            syncPlayer(player);
        }
    }

    /**
     * 背包内的配方书不会触发 InventoryOpenEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRecipeBookToggle(PlayerRecipeBookSettingsChangeEvent event) {
        if (!event.isOpen()) {
            return;
        }
        syncIfPending(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if (!RECIPE_MENU_TYPES.contains(event.getInventory().getType())) {
            return;
        }
        syncIfPending(player);
    }

    /**
     * 进服时服务端本身会下发一次全量配方
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        pendingPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        pendingPlayers.remove(event.getPlayer().getUniqueId());
    }

    private void markAllPending() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            pendingPlayers.add(player.getUniqueId());
        }
    }

    private void syncPlayersWithRecipeMenu() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            InventoryType type = InventoryViewHelper.getTopInventory(player).getType();
            if (RECIPE_MENU_TYPES.contains(type)) {
                syncIfPending(player);
            }
        }
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        if (lifeCycle != LifecyclePhase.DISABLE) {
            return;
        }
        pendingPlayers.clear();
        recipePacket = null;
    }

}
