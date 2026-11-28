package pers.yufiria.craftorithm.recipe;

import crypticlib.CrypticLibBukkit;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pers.yufiria.craftorithm.api.event.CraftorithmReloadDoneEvent;
import pers.yufiria.craftorithm.api.event.RecipeRegisterEvent;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum AutoDiscoverHandler implements Listener, LifecycleTask {

    INSTANCE;

    /**
     * 配方配置中「自动解锁配方书」的开关字段
     */
    public static final String CONFIG_KEY = "auto_discover";

    //注册在区域线程/异步线程都可能发生, 进服读取则在自己的区域线程, 因此用并发集合
    private final Set<NamespacedKey> autoUnlockRecipes = ConcurrentHashMap.newKeySet();

    @EventHandler
    public void onRecipeLoad(RecipeRegisterEvent event) {
        if (!event.recipeConfig().getBoolean(CONFIG_KEY, false)) {
            //关闭时同步移出待解锁集合, 否则改配置后要等到下次重载才生效
            autoUnlockRecipes.remove(event.recipeKey());
            return;
        }
        autoUnlockRecipes.add(event.recipeKey());
        if (event.reason() == RecipeRegisterEvent.Reason.RELOAD) {
            return;
        }
        discoverForOnlinePlayers(Set.of(event.recipeKey()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Set<NamespacedKey> snapshot = Set.copyOf(autoUnlockRecipes);
        if (snapshot.isEmpty()) {
            return;
        }
        CrypticLibBukkit.scheduler().entity(player).submit(() -> player.discoverRecipes(snapshot));
    }

    @EventHandler
    public void onCraftorithmReloadDone(CraftorithmReloadDoneEvent event) {
        discoverForOnlinePlayers(autoUnlockRecipes);
    }

    /**
     * 逐玩家投递解锁。
     * 配方注册与重载可能落在全局区域线程或异步线程, 而配方书只能由玩家自己的区域线程写入,
     * 因此不能直接遍历在线玩家操作, 必须逐个投递到对应实体的调度器上。
     */
    private void discoverForOnlinePlayers(Collection<NamespacedKey> recipeKeys) {
        Set<NamespacedKey> snapshot = Set.copyOf(recipeKeys);
        if (snapshot.isEmpty()) {
            return;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            CrypticLibBukkit.scheduler().entity(player).submit(() -> player.discoverRecipes(snapshot));
        }
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        autoUnlockRecipes.clear();
    }
}
