package top.oasismc.oasisrecipe.config;

import org.bukkit.configuration.file.YamlConfiguration;
import top.oasismc.oasisrecipe.OasisRecipe;

import java.util.*;

public class ConfigUpdater {

    private static final ConfigUpdater UPDATER = new ConfigUpdater();

    private final Map<String, Object> defConfigMap;

    private ConfigUpdater() {
        defConfigMap = new HashMap<>();
        loadDefConfigs();
    }

    public void addConfig(String key, Object defValue) {
        defConfigMap.put(key, defValue);
    }

    private void loadDefConfigs() {
        addConfig("recipeCheck.exp", true);
        addConfig("recipeCheck.vault", true);
        addConfig("recipeCheck.points", true);
        addConfig("recipeCheck.permission", true);
        addConfig("removeVanillaRecipe", false);
    }

    public void updateConfig() {
        YamlConfiguration config = (YamlConfiguration) OasisRecipe.getPlugin().getConfig();
        Set<String> configKeySet = config.getKeys(true);
        Set<String> updateKeys = defConfigMap.keySet();
        updateKeys.removeAll(configKeySet);
        for (String key : updateKeys) {
            config.set(key, defConfigMap.get(key));
        }
        config.set("version", OasisRecipe.getPlugin().getDescription().getVersion());
        OasisRecipe.getPlugin().saveConfig();
    }

    public static ConfigUpdater getInstance() { return UPDATER; }

}
