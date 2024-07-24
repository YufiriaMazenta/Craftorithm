package com.github.yufiriamazenta.craftorithm.menu.display;

import crypticlib.lifecycle.BukkitEnabler;
import crypticlib.lifecycle.BukkitReloader;
import crypticlib.lifecycle.annotation.OnEnable;
import crypticlib.lifecycle.annotation.OnReload;
import crypticlib.ui.display.MenuDisplay;
import org.bukkit.plugin.Plugin;

@OnReload
@OnEnable
public enum DisplayCreator implements BukkitEnabler, BukkitReloader {

    INSTANCE;
    private MenuDisplay craftingMenuDisplay;

    @Override
    public void enable(Plugin plugin) {
        reload(plugin);
    }

    @Override
    public void reload(Plugin plugin) {
        reloadCraftingMenuDisplay();
    }

    private void reloadCraftingMenuDisplay() {

    }

}
