package com.github.yufiriamazenta.craftorithm.hook.impl;

import com.github.yufiriamazenta.craftorithm.hook.PluginHooker;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

@AutoTask(
    rules = @TaskRule(lifeCycle = LifeCycle.ENABLE)
)
public enum VaultHooker implements PluginHooker {

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

}
