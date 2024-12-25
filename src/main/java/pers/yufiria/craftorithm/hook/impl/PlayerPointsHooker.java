package pers.yufiria.craftorithm.hook.impl;

import pers.yufiria.craftorithm.hook.PluginHooker;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import org.black_ixx.playerpoints.PlayerPoints;
import org.jetbrains.annotations.Nullable;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum PlayerPointsHooker implements PluginHooker {

    INSTANCE;
    private Object playerPoints;
    private Boolean playerPointsHooked;

    @Override
    public String pluginName() {
        return "PlayerPoints";
    }

    @Override
    public boolean hook() {
        this.playerPointsHooked = hookByEnabled();
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

}
