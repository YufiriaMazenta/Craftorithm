package com.github.yufiriamazenta.craftorithm.menu.editor;

import crypticlib.lifecycle.BukkitEnabler;
import crypticlib.lifecycle.BukkitReloader;
import crypticlib.lifecycle.annotation.OnEnable;
import crypticlib.lifecycle.annotation.OnReload;
import org.bukkit.plugin.Plugin;

@OnReload
@OnEnable
public enum EditorCreator implements BukkitEnabler, BukkitReloader {

    INSTANCE;

    @Override
    public void enable(Plugin plugin) {

    }

    @Override
    public void reload(Plugin plugin) {

    }

}
