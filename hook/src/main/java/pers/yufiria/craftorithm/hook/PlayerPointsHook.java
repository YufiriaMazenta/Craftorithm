package pers.yufiria.craftorithm.hook;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.script.ScriptEngine;
import org.black_ixx.playerpoints.PlayerPoints;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.script.PlayerPointsModule;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskSettings(
    rules = @LifecycleRule(lifeCycle = Lifecycle.ACTIVE)
)
public enum PlayerPointsHook implements PluginHook, LifecycleTask {

    INSTANCE;
    private Object playerPoints;

    @Override
    public String pluginName() {
        return "PlayerPoints";
    }

    @Override
    public boolean hook() {
        boolean playerPointsHooked = isPluginEnabled();
        if (playerPointsHooked) {
            playerPoints = PlayerPoints.getInstance();
            ScriptEngine.INSTANCE.registerModule(PlayerPointsModule.INSTANCE);
        }
        return playerPointsHooked;
    }

    public @Nullable Object playerPoints() {
        return playerPoints;
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        if (hook()) {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        }
    }

}
