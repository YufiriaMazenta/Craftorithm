package pers.yufiria.craftorithm.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.fakeresult.FakeResultDataHandler;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.ACTIVE),
        @LifecycleRule(lifeCycle = Lifecycle.DISABLE)
    }
)
public enum PacketEventsHook implements PluginHook, LifecycleTask {

    INSTANCE;

    private Object fakeResultPacketListenerCommon = null;

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
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
        fakeResultPacketListenerCommon = eventManager
            .registerListener(FakeResultPreviewPacketListener.INSTANCE, PacketListenerPriority.NORMAL);
        FakeResultDataHandler.INSTANCE.setSupportFakeResult(true);
        return true;
    }

    @Override
    public void unhook() {
        EventManager eventManager = PacketEvents.getAPI().getEventManager();
        if (fakeResultPacketListenerCommon != null) {
            eventManager.unregisterListener((PacketListenerCommon) fakeResultPacketListenerCommon);
        }
    }
}
