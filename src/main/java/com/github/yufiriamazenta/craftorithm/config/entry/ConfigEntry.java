package com.github.yufiriamazenta.craftorithm.config.entry;

import org.bukkit.configuration.ConfigurationSection;

public abstract class ConfigEntry<T> {

    private String key;
    private T value;
    private final T def;

    ConfigEntry(String key, T def) {
        this.key = key;
        this.def = def;
    }

    public T value() {
        return value;
    }

    public ConfigEntry<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public T def() {
        return def;
    }

    public String key() {
        return key;
    }

    public ConfigEntry<T> setKey(String key) {
        this.key = key;
        return this;
    }

    public void saveDef(ConfigurationSection config) {
        if (config.contains(key))
            return;
        config.set(key, def);
    }

    public abstract void load(ConfigurationSection config);

}
