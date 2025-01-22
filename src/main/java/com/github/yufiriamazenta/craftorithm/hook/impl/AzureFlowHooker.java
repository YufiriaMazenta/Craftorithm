package com.github.yufiriamazenta.craftorithm.hook.impl;

import com.github.yufiriamazenta.craftorithm.hook.ItemPluginHooker;
import com.github.yufiriamazenta.craftorithm.item.ItemProvider;
import com.github.yufiriamazenta.craftorithm.item.impl.AzureFlowItemProvider;
import crypticlib.lifecycle.AutoTask;
import crypticlib.lifecycle.LifeCycle;
import crypticlib.lifecycle.TaskRule;

@AutoTask(rules = @TaskRule(lifeCycle = LifeCycle.ACTIVE))
public enum AzureFlowHooker implements ItemPluginHooker {

    INSTANCE;

    @Override
    public ItemProvider itemProvider() {
        return AzureFlowItemProvider.INSTANCE;
    }

    @Override
    public String pluginName() {
        return "AzureFlow";
    }

    @Override
    public boolean hook() {
        return hookByEnabled();
    }

}