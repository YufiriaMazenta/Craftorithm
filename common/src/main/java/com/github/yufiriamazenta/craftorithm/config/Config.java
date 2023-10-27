package com.github.yufiriamazenta.craftorithm.config;

import com.github.yufiriamazenta.craftorithm.Craftorithm;
import org.bukkit.configuration.file.YamlConfiguration;

public interface Config<T> {

    T value();

    void setValue(T value);

    void reload();

    String getKey();

    enum BooleanConfig implements Config<Boolean> {

        CHECK_UPDATE("check_update", true),
        B_STATS("bStats", true)
        ;

        private Boolean value;
        private final Boolean def;
        private final String key;

        BooleanConfig(String key, Boolean def) {
            this.def = def;
            this.key = key;
        }

        @Override
        public Boolean value() {
            return this.value;
        }

        @Override
        public void setValue(Boolean value) {
            this.value = value;
        }

        @Override
        public void reload() {
            YamlConfiguration config = (YamlConfiguration) Craftorithm.getInstance().getConfig();
            if (config.contains(key))
                value = config.getBoolean(key);
            else {
                config.set(key, def);
                value = def;
            }
        }

        @Override
        public String getKey() {
            return key;
        }
    }

}
