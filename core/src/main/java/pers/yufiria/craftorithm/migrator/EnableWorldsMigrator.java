package pers.yufiria.craftorithm.migrator;

import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import crypticlib.util.IOHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.yufiria.craftorithm.Craftorithm;
import pers.yufiria.craftorithm.recipe.RecipeManager;
import pers.yufiria.craftorithm.recipe.worldcondition.WorldConditionHandler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 把旧版限制世界用的 enable_worlds 转换为 world_conditions.name
 * <p>
 * 在插件 ENABLE 阶段执行一次，转换会重写配方文件，文件里的注释不会保留。
 */
@LifecycleTaskConfig(
    schedules = @LifecycleSchedule(phase = LifecyclePhase.ENABLE)
)
public enum EnableWorldsMigrator implements LifecycleTask {

    INSTANCE;

    private static final String NAME_KEY = "name";

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        File recipeFolder = RecipeManager.INSTANCE.RECIPE_FILE_FOLDER;
        if (!recipeFolder.exists()) {
            return;
        }
        Logger logger = Craftorithm.instance().getLogger();
        int migratedFiles = 0;
        for (File recipeFile : IOHelper.allYamlFiles(recipeFolder)) {
            try {
                if (migrateFile(recipeFile, logger)) {
                    migratedFiles++;
                }
            } catch (Throwable throwable) {
                logger.log(
                    Level.WARNING,
                    "Failed to migrate " + WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY
                        + " in recipe file " + recipeFile.getName(),
                    throwable
                );
            }
        }
        if (migratedFiles > 0) {
            logger.info(
                "Migrated " + WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY + " to "
                    + WorldConditionHandler.WORLD_CONDITIONS_KEY + ".name in " + migratedFiles + " recipe file(s)"
            );
        }
    }

    private boolean migrateFile(File recipeFile, Logger logger) throws IOException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(recipeFile);
        if (!config.contains(WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY)) {
            return false;
        }
        if (config.get(WorldConditionHandler.WORLD_CONDITIONS_KEY) != null
            && !config.isConfigurationSection(WorldConditionHandler.WORLD_CONDITIONS_KEY)) {
            logger.warning(
                recipeFile.getName() + " has a " + WorldConditionHandler.WORLD_CONDITIONS_KEY
                    + " that is not a section, skipped migration"
            );
            return false;
        }
        if (config.isConfigurationSection(WorldConditionHandler.WORLD_CONDITIONS_KEY)
            && config.getConfigurationSection(WorldConditionHandler.WORLD_CONDITIONS_KEY).contains(NAME_KEY)) {
            logger.warning(
                recipeFile.getName() + " already configures " + WorldConditionHandler.WORLD_CONDITIONS_KEY
                    + "." + NAME_KEY + ", the deprecated " + WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY
                    + " is dropped"
            );
        }

        convert(config);
        config.save(recipeFile);
        return true;
    }

    /**
     * 把旧配置项转成 {@code world_conditions.name}
     * <p>
     * 已经配置了 {@code name} 时以 {@code name} 为准，只删除旧配置项
     *
     * @return 是否发生了改动
     */
    static boolean convert(ConfigurationSection config) {
        if (!config.contains(WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY)) {
            return false;
        }
        Object legacyValue = config.get(WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY);
        ConfigurationSection worldConditions = config.getConfigurationSection(
            WorldConditionHandler.WORLD_CONDITIONS_KEY
        );
        if (worldConditions == null) {
            //占位的空值会让 createSection 抛错, 先清掉再建
            config.set(WorldConditionHandler.WORLD_CONDITIONS_KEY, null);
            worldConditions = config.createSection(WorldConditionHandler.WORLD_CONDITIONS_KEY);
        }
        if (!worldConditions.contains(NAME_KEY)) {
            worldConditions.set(NAME_KEY, legacyValue);
        }
        config.set(WorldConditionHandler.LEGACY_ENABLE_WORLDS_KEY, null);
        return true;
    }

}
