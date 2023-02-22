package me.yufiria.craftorithm.config;

import me.yufiria.craftorithm.Craftorithm;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum ConfigUpdater {

    INSTANCE;

    private final Map<String, Object> defConfigMap;

    ConfigUpdater() {
        defConfigMap = new HashMap<>();
        loadDefConfigs();
    }

    public void addConfig(String key, Object defValue) {
        defConfigMap.put(key, defValue);
    }

    private void loadDefConfigs() {
        addConfig("check_update", true);
        addConfig("remove_all_vanilla_recipe", false);
        addConfig("lore_cannot_craft", "&c不能参与合成");
        addConfig("all_recipe_unlocked", false);
    }

    public void updateConfig() {
        YamlConfiguration config = (YamlConfiguration) Craftorithm.getInstance().getConfig();
        Set<String> configKeySet = config.getKeys(true);
        Set<String> updateKeys = defConfigMap.keySet();
        updateKeys.removeAll(configKeySet);
        for (String key : updateKeys) {
            config.set(key, defConfigMap.get(key));
        }
        config.set("version", Craftorithm.getInstance().getDescription().getVersion());
        Craftorithm.getInstance().saveConfig();
    }

}
