package pers.yufiria.craftorithm.hook;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.Lifecycle;
import crypticlib.lifecycle.LifecycleRule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskSettings;
import crypticlib.script.ScriptEngine;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.hook.script.VaultModule;
import pers.yufiria.craftorithm.hook.script.VaultUnlockedModule;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@LifecycleTaskSettings(
    rules = @LifecycleRule(lifeCycle = Lifecycle.ACTIVE)
)
public enum VaultHook implements PluginHook, LifecycleTask {

    INSTANCE;

    private Object economy = null;
    private boolean isEconomyUnlocked;

    VaultHook() {
        try {
            Class.forName("net.milkbowl.vault2.economy.Economy");
            isEconomyUnlocked = true;
        } catch (ClassNotFoundException e) {
            isEconomyUnlocked = false;
        }
    }

    @Override
    public String pluginName() {
        if (isEconomyUnlocked) {
            return "VaultUnlocked";
        }
        return "Vault";
    }

    @Override
    public boolean hook() {
        boolean vaultEnabled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (!vaultEnabled) {
            return false;
        }

        if (isEconomyUnlocked) {
            RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> vaultRsp = Bukkit
                .getServer()
                .getServicesManager()
                .getRegistration(net.milkbowl.vault2.economy.Economy.class);
            if (vaultRsp == null) {
                return false;
            }
            economy = vaultRsp.getProvider();
            ScriptEngine.INSTANCE.registerModule(VaultUnlockedModule.INSTANCE);
        } else {
            RegisteredServiceProvider<Economy> vaultRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (vaultRsp == null) {
                return false;
            }
            economy = vaultRsp.getProvider();
            ScriptEngine.INSTANCE.registerModule(VaultModule.INSTANCE);
        }

        return true;
    }

    @Override
    public void unhook() {}

    /**
     * 获取经济插件实现实例
     * @return 经济插件的实例,如果挂钩失败将返回null
     */
    public @Nullable Object economy() {
        return economy;
    }

    @Override
    public void lifecycle(CrypticLibPlugin plugin, Lifecycle lifeCycle) {
        if (hook()) {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        }
    }

}
