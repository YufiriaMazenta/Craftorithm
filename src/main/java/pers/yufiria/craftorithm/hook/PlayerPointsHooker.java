package pers.yufiria.craftorithm.hook;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum PlayerPointsHooker implements PluginHooker, BukkitLifeCycleTask {

    INSTANCE;
    private Object playerPoints;
    private Boolean playerPointsHooked;

    @Override
    public String pluginName() {
        return "PlayerPoints";
    }

    @Override
    public boolean hook() {
        this.playerPointsHooked = isPluginEnabled();
        if (playerPointsHooked) {
            playerPoints = PlayerPoints.getInstance();
        }
        return playerPointsHooked;
    }

    public @Nullable Object playerPoints() {
        return playerPoints;
    }

    /**
     * 点券插件是否挂钩成功
     * @return 挂钩结果
     */
    public Boolean isPlayerPointsHooked() {
        return playerPointsHooked;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        if (hook()) {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        }
    }

}
