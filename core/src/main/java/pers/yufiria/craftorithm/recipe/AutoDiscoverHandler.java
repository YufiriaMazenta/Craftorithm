package pers.yufiria.craftorithm.recipe;

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

import java.util.HashSet;
import java.util.Set;

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

    private final Set<NamespacedKey> autoUnlockRecipes = new HashSet<>();

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
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.discoverRecipe(event.recipeKey());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().discoverRecipes(autoUnlockRecipes);
    }

    @EventHandler
    public void onCraftorithmReloadDone(CraftorithmReloadDoneEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.discoverRecipes(autoUnlockRecipes);
        }
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        autoUnlockRecipes.clear();
    }
}
