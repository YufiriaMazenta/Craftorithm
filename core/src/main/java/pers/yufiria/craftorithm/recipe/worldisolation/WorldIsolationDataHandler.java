package pers.yufiria.craftorithm.recipe.worldisolation;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import pers.yufiria.craftorithm.api.event.RecipeLoadFromConfigEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventListener
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE)
    }
)
public enum WorldIsolationDataHandler implements Listener, LifecycleTask {

    INSTANCE;

    private final Map<NamespacedKey, EnableWorlds> recipeEnableWorldsMap = new ConcurrentHashMap<>();
    public final static String ENABLE_WORLDS_CONFIG_KEY = "enable_worlds";

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        recipeEnableWorldsMap.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRecipeLoad(RecipeLoadFromConfigEvent event) {
        if (event.isCancelled()) {
            return;
        }
        YamlConfiguration recipeConfig = event.recipeConfig();
        if (!recipeConfig.contains(ENABLE_WORLDS_CONFIG_KEY)) {
            return;
        }

        EnableWorlds enableWorlds;
        if (recipeConfig.isString(ENABLE_WORLDS_CONFIG_KEY)) {
            String worldName = recipeConfig.getString(ENABLE_WORLDS_CONFIG_KEY);
            if (!isWorldValid(worldName)) {
                CrypticLib.info("&c" + worldName + " is a invalid world in recipe: " + event.recipeKey());
                return;
            }
            enableWorlds = new EnableWorlds(Collections.singletonList(worldName));
        } else if (recipeConfig.isList(ENABLE_WORLDS_CONFIG_KEY)) {
            List<String> enableWorldNameList = recipeConfig.getStringList(ENABLE_WORLDS_CONFIG_KEY);
            enableWorldNameList.removeIf(it -> {
                boolean worldValid = isWorldValid(it);
                if (!worldValid) {
                    CrypticLib.info("&c" + it + " is a invalid world in recipe: " + event.recipeKey());
                }
                return !worldValid;
            });
            enableWorlds = new EnableWorlds(enableWorldNameList);
        } else {
            return;
        }
        recipeEnableWorldsMap.put(event.recipeKey(), enableWorlds);
    }

    public boolean canRecipeUse(NamespacedKey recipeKey, World world) {
        //如果没有相关设置，默认即为允许所有世界合成
        if (!recipeEnableWorldsMap.containsKey(recipeKey)) {
            return true;
        }
        EnableWorlds enableWorlds = recipeEnableWorldsMap.get(recipeKey);
        return enableWorlds.worlds.contains(world.getName());
    }

    private boolean isWorldValid(String worldName) {
        if (worldName == null) {
            return false;
        }
        return Bukkit.getWorld(worldName) != null;
    }

    public record EnableWorlds(List<String> worlds) {}

}
