package pers.yufiria.craftorithm.recipe.sync;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
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
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.recipe.nms.NmsRecipeAccessor;

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
    //当前最新配方包, 具体类型由当前版本的NmsRecipeAccessor决定
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
        NmsRecipeAccessor nmsRecipeAccessor = NmsRecipeAccessor.findImpl();
        if (!PluginConfigs.LAZY_RECIPE_SYNC.value()) {
            nmsRecipeAccessor.syncRecipes();
            return;
        }
        nmsRecipeAccessor.rebuildRecipes();
        nmsRecipeAccessor.buildRecipePacket();
        markAllPending();
        //重建后展示id会整体位移, 正在使用配方面板的玩家必须立即刷新
        syncPlayersWithRecipeMenu();
    }

    /**
     * 立即向单个玩家下发配方数据
     */
    public void syncPlayer(Player player) {
        pendingPlayers.remove(player.getUniqueId());
        if (NmsRecipeAccessor.findImpl().syncRecipes(player)) {
            return;
        }
        //不支持逐玩家下发时退化为全量推送
        if (unsupportedWarned.compareAndSet(false, true)) {
            CrypticLib.warn("Per-player recipe sync is not supported on this server, fallback to full update");
            NmsRecipeAccessor.findImpl().syncRecipes();
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
     * 玩家打开配方书后补发
     * <p>
     * 客户端此时还在重排配方书页面, 立刻整包替换会让页面停在空白状态, 所以延后几tick
     */
    public void syncAfterRecipeBookOpen(Player player) {
        if (!pendingPlayers.contains(player.getUniqueId())) {
            return;
        }
        CrypticLibBukkit.scheduler().runOnEntityLater(player, () -> syncIfPending(player), 2);
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
