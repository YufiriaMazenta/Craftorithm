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
        addConfig("recipeCheck.exp", true);
        addConfig("recipeCheck.vault", true);
        addConfig("recipeCheck.points", true);
        addConfig("recipeCheck.permission", true);
        addConfig("removeVanillaRecipe.crafting_table", false);
        addConfig("removeVanillaRecipe.furnace", false);
        addConfig("removeVanillaRecipe.blasting", false);
        addConfig("removeVanillaRecipe.smoking", false);
        addConfig("removeVanillaRecipe.campfire", false);
        addConfig("removeVanillaRecipe.smithing", false);
        addConfig("removeVanillaRecipe.stoneCutting", false);
        addConfig("removeVanillaRecipe.merchant", false);
        addConfig("removeVanillaRecipe.others", false);
        addConfig("messages.load.itemsAdderSuccess", "&a发现ItemsAdder,已挂钩");
        addConfig("messages.load.itemsAdderFailed", "&c未发现ItemsAdder");
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

}
