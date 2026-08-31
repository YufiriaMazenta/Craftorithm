package pers.yufiria.craftorithm.hook.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.*;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.fakeresult.FakeResultDataHandler;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskConfig(schedules = {
    @LifecycleSchedule(phase = LifecyclePhase.ACTIVE),
    @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
})
public enum ProtocolLibHook implements PluginHook, LifecycleTask {

    INSTANCE;

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
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
        return "ProtocolLib";
    }

    @Override
    public boolean hook() {
        if (!isPluginEnabled()) {
            return false;
        }
        LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        //注册虚假合成结果预览数据包监听器
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(FakeResultPreviewPacketListener.INSTANCE);
        FakeResultDataHandler.INSTANCE.setSupportFakeResult(true);
        return true;
    }

    @Override
    public void unhook() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(Craftorithm.instance());
    }

}
