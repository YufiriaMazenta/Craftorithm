package pers.yufiria.craftorithm.migrator;

import crypticlib.CrypticLib;
import crypticlib.CrypticLibPlugin;
import crypticlib.config.BukkitConfigWrapper;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.yufiria.craftorithm.Craftorithm;

/**
 * 旧版已解锁配方同步配置迁移器
 * <p>
 * 将 recipe_discovery_sync 配置段迁移为 save_discovered_recipes 配置段
 * <p>
 * 注意: enable 不参与迁移, 使其使用新键的默认值
 */
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.INIT)
    }
)
public enum LegacyDiscoveredRecipeSyncMigrator implements LifecycleTask {

    INSTANCE;

    private static final String OLD_SECTION = "recipe_discovery_sync";
    private static final String NEW_SECTION = "save_discovered_recipes";

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        BukkitConfigWrapper configWrapper = ((Craftorithm) plugin).getConfigWrapperOrCreate("config.yml");
        YamlConfiguration config = configWrapper.config();

        if (!config.contains(OLD_SECTION)) {
            return;
        }

        int migrated = 0;
        if (migrateOption(config, "join_sync_delay_ticks", "join_discover_delay_ticks")) {
            migrated++;
        }
        if (migrateOption(config, "interval_ticks", "interval_ticks")) {
            migrated++;
        }

        //旧配置段已整体废弃, 无论是否有可迁移的键都移除
        config.set(OLD_SECTION, null);
        configWrapper.saveConfig();
        configWrapper.reloadConfig();
        CrypticLib.info("Migrated " + migrated + " option(s) from " + OLD_SECTION + " to " + NEW_SECTION);
    }

    /**
     * 将旧配置段下的某个键迁移到新配置段
     * 只迁移旧键实际存在的项, 未配置过的项继续使用新键的默认值
     * @return 旧键存在并完成迁移时返回true
     */
    private boolean migrateOption(YamlConfiguration config, String oldKey, String newKey) {
        String oldPath = OLD_SECTION + "." + oldKey;
        if (!config.contains(oldPath)) {
            return false;
        }
        config.set(NEW_SECTION + "." + newKey, config.get(oldPath));
        return true;
    }

}