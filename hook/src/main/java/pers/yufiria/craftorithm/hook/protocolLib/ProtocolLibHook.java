package pers.yufiria.craftorithm.hook.protocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import crypticlib.CrypticLibPlugin;
import crypticlib.MinecraftVersion;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import org.bukkit.Bukkit;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.PluginHook;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskSettings(
    rules = {
        @LifecycleRule(lifeCycle = Lifecycle.ACTIVE),
        @LifecycleRule(lifeCycle = Lifecycle.DISABLE)
    }
)
public enum ProtocolLibHook implements PluginHook, LifecycleTask {

    INSTANCE;

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
        return "ProtocolLib";
    }

    @Override
    public boolean hook() {
        if (!isPluginEnabled()) {
            return false;
        }
        LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        Bukkit.getPluginManager().registerEvents(FakeResultPreviewPacketListener.INSTANCE, Craftorithm.instance());
        //注册虚假合成结果预览数据包监听器
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(FakeResultPreviewPacketListener.INSTANCE);
        //注册配方书数据包监听器
        PacketType[] handlePacketTypes;
        if (MinecraftVersion.current().afterOrEquals(MinecraftVersion.V1_21_2)) {
            handlePacketTypes = new PacketType[] {
                PacketType.Play.Server.RECIPE_BOOK_ADD,
                PacketType.Play.Server.RECIPE_BOOK_REMOVE,
                PacketType.Play.Server.RECIPE_BOOK_SETTINGS,
                PacketType.Play.Server.RECIPE_UPDATE,
                PacketType.Play.Server.ADVANCEMENTS,
                PacketType.Play.Server.TAGS
            };
        } else {
            handlePacketTypes = new PacketType[] {
                PacketType.Play.Server.RECIPE_UPDATE,
                PacketType.Play.Server.ADVANCEMENTS,
                PacketType.Play.Server.TAGS
            };
        }
        protocolManager.addPacketListener(new PacketAdapter(
            Craftorithm.instance(),
            ListenerPriority.NORMAL,
            handlePacketTypes
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

    @Override
    public void unhook() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(Craftorithm.instance());
    }

}
