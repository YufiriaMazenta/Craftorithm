package pers.yufiria.craftorithm;

import crypticlib.BukkitPlugin;
import crypticlib.CrypticLib;
import crypticlib.CrypticLibBukkit;
import crypticlib.MinecraftVersion;
import crypticlib.action.ActionCompiler;
import crypticlib.chat.BukkitMsgSender;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import crypticlib.util.IOHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.bstat.Metrics;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.config.PluginConfigs;
import pers.yufiria.craftorithm.exception.UnsupportedVersionException;
import pers.yufiria.craftorithm.hook.listener.OtherPluginsListenerManager;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.ui.action.Back;
import pers.yufiria.craftorithm.ui.action.Close;
import pers.yufiria.craftorithm.ui.action.OpenMenu;
import pers.yufiria.craftorithm.util.LangUtils;
import pers.yufiria.craftorithm.util.UpdateChecker;

@AutoTask(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE, priority = 2),
        @TaskRule(lifeCycle = LifeCycle.RELOAD)
    }
)
public final class Craftorithm extends BukkitPlugin implements BukkitLifeCycleTask {

    private static Craftorithm INSTANCE;

    public Craftorithm() {
        INSTANCE = this;
    }

    @Override
    public void enable() {
        CrypticLib.setDebug(PluginConfigs.DEBUG.value());
        IOHelper.info("&7Server Type: " + CrypticLibBukkit.serverAdapter().type() + ", Version: " + MinecraftVersion.current().version());
        if (MinecraftVersion.current().before(MinecraftVersion.V1_19_4)) {
            BukkitMsgSender.INSTANCE.info("&cUnsupported Version");
            throw new UnsupportedVersionException();
        }
        ActionCompiler.INSTANCE.regAction("close", Close::new);
        ActionCompiler.INSTANCE.regAction("openmenu", OpenMenu::new);
        ActionCompiler.INSTANCE.regAction("back", Back::new);
        UpdateChecker.pullUpdateCheckRequest(Bukkit.getConsoleSender());
    }

    @Override
    public void disable() {
        RecipeManager.INSTANCE.resetRecipes();
    }

    private void loadBStat() {
        if (!PluginConfigs.BSTATS.value())
            return;
        Metrics metrics = new Metrics(this, 17821);
        metrics.addCustomChart(new Metrics.SingleLineChart("recipes", () -> RecipeManager.INSTANCE.getRecipeGroups().size()));
    }

    public static Craftorithm instance() {
        return INSTANCE;
    }

    public static CraftorithmAPI api() {
        return CraftorithmAPI.INSTANCE;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        if (lifeCycle == LifeCycle.ACTIVE) {
            CrypticLibBukkit.scheduler().sync(() -> {
                RecipeManager.INSTANCE.reloadRecipeManager();
                OtherPluginsListenerManager.INSTANCE.convertOtherPluginsListeners();
                LangUtils.info(Languages.LOAD_FINISH);
                loadBStat();
            });
        } else {
            CrypticLib.setDebug(PluginConfigs.DEBUG.value());
        }
    }

}
