package pers.yufiria.craftorithm;

import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.*;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.api.CraftorithmAPI;
import pers.yufiria.craftorithm.bstat.Metrics;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.exception.UnsupportedVersionException;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.RecipeType;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.UpdateChecker;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE, priority = 2),
    }
)
public final class Craftorithm extends BukkitPlugin implements LifeCycleTask {

    private static Craftorithm INSTANCE;

    public Craftorithm() {
        INSTANCE = this;
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
        UpdateChecker.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }

    @Override
    public void whenReload() {
        CrypticLib.debug = PluginConfigs.DEBUG.value();
    }

    @Override
    public void whenDisable() {
        RecipeManager.INSTANCE.resetRecipes();
    }

    private void loadBStat() {
        if (!PluginConfigs.BSTATS.value())
            return;
        Metrics metrics = new Metrics(this, 17821);
        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.INSTANCE.getRecipeGroups().size()));
        metrics.addCustomChart(new Metrics.AdvancedPie("recipe_type_count", () -> {
            Map<String, Integer> map = new HashMap<>();
            for (Recipe recipe : RecipeManager.INSTANCE.craftorithmRecipes().values()) {
                RecipeType type = RecipeManager.INSTANCE.getRecipeType(recipe);
                map.merge(type.typeKey(), 1, Integer::sum);
            }
            return map;
        }));
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    public static CraftorithmAPI api() {
        return CraftorithmAPI.INSTANCE;
    }

    @Override
    public void lifecycle(Object plugin, LifeCycle lifeCycle) {
        CrypticLibBukkit.scheduler().sync(() -> {
            RecipeManager.INSTANCE.reloadRecipeManager();
            LangUtils.info(Languages.LOAD_FINISH);
            loadBStat();
        });
    }

}
