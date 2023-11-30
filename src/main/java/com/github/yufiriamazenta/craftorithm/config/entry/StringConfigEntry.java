package com.github.yufiriamazenta.craftorithm.config.entry;

import org.bukkit.configuration.ConfigurationSection;

public class StringConfigEntry extends ConfigEntry<String> {
    public StringConfigEntry(String key, String def) {
        super(key, def);
    }

    @Override
    public void load(ConfigurationSection config) {
        if (!config.contains(key())) {
            config.set(key(), def());
            setValue(def());
            return;
        }
        setValue(config.getString(key()));
    }

}
