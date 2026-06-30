package pers.yufiria.craftorithm.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ACTIVE),
        @TaskRule(lifeCycle = LifeCycle.DISABLE)
    }
)
public enum PacketEventsHook implements PluginHook, BukkitLifeCycleTask {

    INSTANCE;

    private Object recipeUpdatePacketListenerCommon = null, fakeResultPacketListenerCommon = null;

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        switch (lifeCycle) {
            case ACTIVE -> {
                hook();
            }
            case DISABLE -> {
                unhook();
            }
        }
    }

    @Override
    public String pluginName() {
        return "packetevents";
    }

    @Override
    public boolean hook() {
        if (!isPluginEnabled()) {
            return false;
        }
        LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        //注册数据包监听器
        EventManager eventManager = PacketEvents
            .getAPI()
            .getEventManager();
        recipeUpdatePacketListenerCommon = eventManager
            .registerListener(RecipeUpdatePacketListener.INSTANCE, PacketListenerPriority.NORMAL);
        fakeResultPacketListenerCommon = eventManager
            .registerListener(FakeResultPreviewPacketListener.INSTANCE, PacketListenerPriority.NORMAL);
        Bukkit.getPluginManager().registerEvents(FakeResultPreviewPacketListener.INSTANCE, Craftorithm.instance());
        return true;
    }

    @Override
    public void unhook() {
        EventManager eventManager = PacketEvents.getAPI().getEventManager();
        if (recipeUpdatePacketListenerCommon != null) {
            eventManager.unregisterListener((PacketListenerCommon) recipeUpdatePacketListenerCommon);
        }
        if (fakeResultPacketListenerCommon != null) {
            eventManager.unregisterListener((PacketListenerCommon) fakeResultPacketListenerCommon);
        }
    }
}
