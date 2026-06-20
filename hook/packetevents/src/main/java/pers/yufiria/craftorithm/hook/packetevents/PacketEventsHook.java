package pers.yufiria.craftorithm.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.hook.PluginHook;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.DISABLE)
    }
)
public enum PacketEventsHook implements PluginHook, BukkitLifeCycleTask {

    INSTANCE;

    private Object listenerCommon = null;

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        switch (lifeCycle) {
            case ENABLE -> {
                hook();
            }
            case DISABLE -> {
                if (listenerCommon != null) {
                    PacketEvents.getAPI().getEventManager().unregisterListener((PacketListenerCommon) listenerCommon);
                }
            }
        }
    }

    @Override
    public String pluginName() {
        return "";
    }

    @Override
    public boolean hook() {
        if (!isPluginEnabled()) {
            return false;
        }
        //注册数据包监听器
        listenerCommon = PacketEvents.getAPI().getEventManager().registerListener(RecipeUpdatePacketListener.INSTANCE, PacketListenerPriority.NORMAL);
        return true;
    }
}
