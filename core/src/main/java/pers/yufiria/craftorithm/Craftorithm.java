package pers.yufiria.craftorithm;

import crypticlib.*;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.log.LogLevel;
import crypticlib.script.ScriptEngine;
import org.bukkit.Bukkit;
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

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, priority = 2),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, priority = Integer.MAX_VALUE, isAsync = true)
    }
)
public final class Craftorithm extends BukkitPlugin implements LifecycleTask {

    private static Craftorithm INSTANCE;

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
        //重载状态置位与超时兜底统一由重载管理器负责
        ReloadManager.INSTANCE.onReloadStart();
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
                    ReloadManager.INSTANCE.reloadPlugin(Bukkit.getConsoleSender());
                    loadBStat();
                    LangUtils.info(Languages.LOAD_FINISH);
                });
            }
            //RELOAD异步批收尾: 等待配方完成、清理重载状态、消费重载队列, 统一由重载管理器负责
            case RELOAD -> ReloadManager.INSTANCE.onReloadFinish();
        }

    }

}
