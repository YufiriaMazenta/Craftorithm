package top.oasismc.oasisrecipe.config;

import org.bukkit.configuration.file.YamlConfiguration;
import top.oasismc.oasisrecipe.OasisRecipe;

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
        addConfig("messages.load.itemsAdderSuccess", "&a发现ItemsAdder,已挂钩");
        addConfig("messages.load.itemsAdderFailed", "&c未发现ItemsAdder");
        addConfig("messages.commands.notExist", "&c配方不存在");
        addConfig("messages.commands.removed", "&a配方已移除");
        addConfig("messages.commands.reloadConfig", "&a配置文件重载成功");
        addConfig("checkUpdate", true);
        addConfig("messages.update.newVersion", "&a检测到有新版本发布, 请及时更新到新版本");
    }

    public void updateConfig() {
        YamlConfiguration config = (YamlConfiguration) OasisRecipe.getInstance().getConfig();
        Set<String> configKeySet = config.getKeys(true);
        Set<String> updateKeys = defConfigMap.keySet();
        updateKeys.removeAll(configKeySet);
        for (String key : updateKeys) {
            config.set(key, defConfigMap.get(key));
        }
        config.set("version", OasisRecipe.getInstance().getDescription().getVersion());
        OasisRecipe.getInstance().saveConfig();
    }

}
