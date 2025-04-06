package pers.yufiria.craftorithm.hook;

import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.BukkitLifeCycleTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;
import pers.yufiria.craftorithm.config.Languages;
import pers.yufiria.craftorithm.util.LangUtils;

import java.util.Map;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE)
)
public enum VaultHooker implements PluginHooker, BukkitLifeCycleTask {

    INSTANCE;

    private Object economy = null;
    private Boolean economyHooked;

    @Override
    public String pluginName() {
        return "Vault";
    }

    @Override
    public boolean hook() {
        boolean vaultEnabled = Bukkit.getPluginManager().isPluginEnabled("Vault");
        if (!vaultEnabled) {
            this.economyHooked = false;
            return false;
        }
        RegisteredServiceProvider<Economy> vaultRsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (vaultRsp == null) {
            this.economyHooked = false;
            return false;
        }
        economy = vaultRsp.getProvider();
        this.economyHooked = true;
        return true;
    }

    /**
     * 获取经济插件实现实例
     * @return 经济插件的实例,如果挂钩失败将返回null
     */
    public @Nullable Object economy() {
        return economy;
    }

    /**
     * 经济插件是否挂钩成功
     * @return 挂钩结果
     */
    public boolean isEconomyHooked() {
        return economyHooked;
    }

    @Override
    public void lifecycle(Plugin plugin, LifeCycle lifeCycle) {
        if (hook()) {
            LangUtils.info(Languages.LOAD_HOOK_PLUGIN_SUCCESS, Map.of("<plugin>", pluginName()));
        }
    }

}
