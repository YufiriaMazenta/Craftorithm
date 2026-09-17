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
 */
@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.INIT),
        @LifecycleSchedule(phase = LifecyclePhase.LOAD)
    }
)
public enum LegacyDiscoveredRecipeSyncMigrator implements LifecycleTask {

    INSTANCE;

    private static final String OLD_SECTION = "recipe_discovery_sync";
    private static final String NEW_SECTION = "save_discovered_recipes";

    /**
     * 本次启动是否执行过迁移
     */
    private boolean migratedThisStartup = false;

    @Override
    public void onLifecycle(CrypticLibPlugin plugin, LifecyclePhase lifeCycle) {
        switch (lifeCycle) {
            case INIT -> migrate(plugin);
            case LOAD -> {
                //迁移时新键还没有注释, 默认注释要等配置节点加载后才会写入内存, 因此这里补一次保存让其落盘
                if (migratedThisStartup) {
                    migratedThisStartup = false;
                    ((Craftorithm) plugin).getConfigWrapperOrCreate("config.yml").saveConfig();
                }
            }
            default -> {}
        }
    }

    private void migrate(CrypticLibPlugin plugin) {
        BukkitConfigWrapper configWrapper = ((Craftorithm) plugin).getConfigWrapperOrCreate("config.yml");
        YamlConfiguration config = configWrapper.config();

        if (!config.contains(OLD_SECTION)) {
            return;
        }

        int migrated = 0;
        if (migrateOption(config, "enable", "enable")) {
            migrated++;
        }
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
        migratedThisStartup = true;
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