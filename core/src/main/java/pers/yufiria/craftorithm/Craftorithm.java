package pers.yufiria.craftorithm;

import com.google.common.base.Supplier;
import crypticlib.*;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.script.ScriptEngine;
import crypticlib.util.IOHelper;
import dev.faststats.Metrics;
import dev.faststats.bukkit.BukkitContext;
import dev.faststats.data.Metric;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import pers.yufiria.craftorithm.api.CraftorithmAPI;
import pers.yufiria.craftorithm.metrics.BStats;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.exception.UnsupportedVersionException;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.script.ActionModule;
import pers.yufiria.craftorithm.script.ConditionModule;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.UpdateChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.ACTIVE, priority = 2),
    }
)
public final class Craftorithm extends BukkitPlugin implements LifecycleTask {

    private static Craftorithm INSTANCE;
    private BukkitContext fastBStatsContext;

    public Craftorithm() {
        INSTANCE = this;
        CrypticLib.debug = true;
    }

    @Override
    public void whenLoad() {
        CrypticLib.debug = PluginConfigs.DEBUG.value();
        IOHelper.info("&7Server Type: " + CrypticLibBukkit.serverAdapter().type() + ", Version: " + MinecraftVersion.current().version());
        if (MinecraftVersion.current().before(MinecraftVersion.V1_20)) {
            BukkitMsgSender.INSTANCE.info("&cUnsupported Version");
            throw new UnsupportedVersionException();
        }
    }

    @Override
    public void whenEnable() {
        ScriptEngine.INSTANCE.registerModule(ActionModule.INSTANCE);
        ScriptEngine.INSTANCE.registerModule(ConditionModule.INSTANCE);
        UpdateChecker.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }

    @Override
    public void whenReload() {
        CrypticLib.debug = PluginConfigs.DEBUG.value();
    }

    @Override
    public void whenDisable() {
        RecipeManager.INSTANCE.resetRecipes();
        if (fastBStatsContext != null) {
            fastBStatsContext.shutdown();
        }
    }

    private void loadMetrics() {
        Callable<Map<String, Integer>> recipeTypeCountMapCallable = () -> {
            Map<String, Integer> map = new HashMap<>();
            for (Recipe recipe : RecipeManager.INSTANCE.craftorithmRecipes().values()) {
                RecipeType type = RecipeManager.INSTANCE.getRecipeType(recipe);
                map.merge(type.typeKey(), 1, Integer::sum);
            }
            return map;
        };
        if (PluginConfigs.METRICS_BSTATS.value()) {
            BStats bStats = new BStats(this, 17821);
            bStats.addCustomChart(new BStats.SingleLineChart("recipes", () -> RecipeManager.INSTANCE.craftorithmRecipes().size()));
            bStats.addCustomChart(new BStats.AdvancedPie("recipe_type_count", recipeTypeCountMapCallable));
        }
        if (PluginConfigs.METRICS_FAST_STATS.value()) {
            fastBStatsContext = new BukkitContext.Factory(this, "ad8d52b8cd8e754987ff6a6b2590802d")
                .metrics(factory -> factory
                    .addMetric(Metric.number("recipes", () -> RecipeManager.INSTANCE.craftorithmRecipes().size()))
                    .addMetric(Metric.numberMap("recipe_type_cound", recipeTypeCountMapCallable))
                    .create())
                .create();
            fastBStatsContext.ready();
        }
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    public static CraftorithmAPI api() {
        return CraftorithmAPI.INSTANCE;
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        CrypticLibBukkit.scheduler().sync(() -> {
            RecipeManager.INSTANCE.reloadRecipeManager();
            LangUtils.info(Languages.LOAD_FINISH);
            loadMetrics();
        });
    }

}
