package com.github.yufiriamazenta.craftorithm.config.entry;

import org.bukkit.configuration.ConfigurationSection;

public class BooleanConfigEntry extends ConfigEntry<Boolean> {

    public BooleanConfigEntry(String key, Boolean def) {
        super(key, def);
    }

    @Override
    public void load(ConfigurationSection config) {
        if (!config.contains(key())) {
            config.set(key(), value());
            setValue(def());
            return;
        }
        setValue(config.getBoolean(key()));
    }

}
