package pers.yufiria.craftorithm.hook.protocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.LifeCycleTaskSettings;
import crypticlib.lifecycle.TaskRule;
import org.bukkit.plugin.Plugin;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifeCycleTaskSettings(
    rules = {
        @TaskRule(lifeCycle = LifeCycle.ENABLE),
        @TaskRule(lifeCycle = LifeCycle.DISABLE)
    }
)
public enum ProtocolLibHook implements PluginHook, BukkitLifeCycleTask {

    INSTANCE;

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        switch (lifeCycle) {
            case ENABLE -> {
                hook();
            }
            case DISABLE -> {
                ProtocolLibrary.getProtocolManager().removePacketListeners(Craftorithm.instance());
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
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(
            Craftorithm.instance(),
            ListenerPriority.NORMAL,
            PacketType.Play.Server.RECIPE_BOOK_ADD,
            PacketType.Play.Server.RECIPE_BOOK_REMOVE,
            PacketType.Play.Server.RECIPE_BOOK_SETTINGS,
            PacketType.Play.Server.RECIPE_UPDATE
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (RecipeManager.INSTANCE.isReloadingRecipeManager()) {
                    event.setCancelled(true);
                }
            }
        });
        return true;
    }

}
