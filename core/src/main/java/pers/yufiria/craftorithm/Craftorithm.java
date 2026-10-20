package pers.yufiria.craftorithm;

import crypticlib.*;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.log.LogLevel;
import crypticlib.scheduler.CrypticLibRunnable;
import crypticlib.script.ScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.api.CraftorithmApi;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.metrics.BStats;
import pers.yufiria.craftorithm.recipe.ParsedRecipe;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.script.ActionModule;
import pers.yufiria.craftorithm.script.ConditionModule;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.UpdateChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, priority = 2),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, priority = Integer.MAX_VALUE, isAsync = true)
    }
)
public final class Craftorithm extends BukkitPlugin implements LifecycleTask {

    private static Craftorithm INSTANCE;
    private volatile @Nullable UUID reloadSenderUuid;
    private final AtomicBoolean reloading = new AtomicBoolean(false);
    private CrypticLibRunnable reloadTimeoutCallback = null;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void whenLoad() {
        CrypticLib.logger().setLevel(LogLevel.valueOf(PluginConfigs.LOG_LEVEL.value().toUpperCase()));
        CrypticLib.info("Server Type: " + CrypticLibBukkit.serverAdapter().type().name().toLowerCase() + ", Version: " + MinecraftVersion.current().versionStr());
    }

    @Override
    public void whenEnable() {
        ScriptEngine.INSTANCE.registerModule(ActionModule.INSTANCE);
        ScriptEngine.INSTANCE.registerModule(ConditionModule.INSTANCE);
        UpdateChecker.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }

    @Override
    public void whenReload() {
        CrypticLib.logger().setLevel(LogLevel.valueOf(PluginConfigs.LOG_LEVEL.value().toUpperCase()));
        reloading.set(true);
        //通过一个延迟任务，确保就算插件重载报错了也能在30秒的超时时间后正确恢复不在重载的状态
        reloadTimeoutCallback = new CrypticLibRunnable() {
            @Override
            public void run() {
                reloading.set(false);
                reloadSenderUuid = null;
            }
        };
        reloadTimeoutCallback.asyncLater(600);
    }

    private void loadBStat() {
        if (!PluginConfigs.BSTATS.value())
            return;
        BStats bStats = new BStats(this, 17821);
        bStats.addCustomChart(new BStats.SingleLineChart("recipes", () -> RecipeManager.INSTANCE.craftorithmRecipes().size()));
        bStats.addCustomChart(new BStats.AdvancedPie("recipe_type_count", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (ParsedRecipe parsed : RecipeManager.INSTANCE.craftorithmRecipes().values()) {
                RecipeType type = parsed.recipeType();
                map.merge(type.typeKey(), 1, Integer::sum);
            }
            return map;
        }));
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    public static CraftorithmApi api() {
        return CraftorithmApi.INSTANCE;
    }

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case ACTIVE -> {
                CrypticLibBukkit.scheduler().sync(() -> {
                    reloadPlugin(Bukkit.getConsoleSender());
                    loadBStat();
                    LangUtils.info(Languages.LOAD_FINISH);
                });
            }
            case RELOAD -> {
                //带超时等待配方重载完成, 防止配方重载链路异常中断导致收尾永久阻塞
                boolean recipeReloadDone = true;
                try {
                    RecipeManager.INSTANCE.getReloadCompletion().get(30, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    recipeReloadDone = false;
                    CrypticLib.error("Timed out waiting for recipe reload completion, skip reload success message");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    recipeReloadDone = false;
                } catch (ExecutionException e) {
                    recipeReloadDone = false;
                    CrypticLib.error("Recipe reload completion ended exceptionally", e.getCause());
                }
                CommandSender sender = getReloader();
                //如果正确重载了，取消超时任务
                if (reloadTimeoutCallback != null) {
                    reloadTimeoutCallback.cancel();
                    reloadTimeoutCallback = null;
                }
                reloadSenderUuid = null;
                reloading.set(false);
                if (recipeReloadDone && sender != null) {
                    LangUtils.sendLang(sender, Languages.COMMAND_RELOAD_SUCCESS);
                }
            }
        }

    }

    public @Nullable CommandSender getReloader() {
        UUID uuid = reloadSenderUuid;
        if (uuid == null || uuid.equals(Invoker.CONSOLE_UUID)) {
            return Bukkit.getConsoleSender();
        }
        return Bukkit.getPlayer(uuid);
    }

    /**
     * 以指定的重载者发起一次插件重载
     * 已经处于重载中时拦截本次重载, 不做任何事
     * <p>
     * reloading状态的置位在{@link #whenReload()}中完成(即reloadConfig之后),
     * 避免reloadConfig抛异常时状态被提前置位且无超时兜底导致永久拦截
     *
     * @param reloader 重载发起者
     * @return 是否发起了重载, 处于重载中被拦截时返回false
     */
    public boolean reloadPlugin(CommandSender reloader) {
        if (reloading.get()) {
            return false;
        }
        reloadSenderUuid = reloader instanceof Player player
            ? player.getUniqueId()
            : Invoker.CONSOLE_UUID;
        reloadPlugin();
        return true;
    }

    public boolean isReloading() {
        return reloading.get();
    }


}
