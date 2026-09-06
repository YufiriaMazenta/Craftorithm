package pers.yufiria.craftorithm.database;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import crypticlib.CrypticLibPlugin;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import pers.yufiria.craftorithm.config.DatabaseConfigs;
import pers.yufiria.craftorithm.database.exception.DatabaseLoadException;
import pers.yufiria.craftorithm.database.loader.DataSourceLoader;
import pers.yufiria.craftorithm.database.loader.MysqlDataSourceLoader;
import pers.yufiria.craftorithm.database.loader.SqliteDataSourceLoader;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.ACTIVE, isAsync = true, priority = -1),
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true, priority = -1),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE, priority = Integer.MAX_VALUE)
    }
)
public enum DataSourceManager implements LifecycleTask {

    INSTANCE;
    private final Map<String, DataSourceLoader> databaseLoaderMap = new ConcurrentHashMap<>();
    private ConnectionSource databaseConnection;

    DataSourceManager() {
        registerDatabaseLoader("mysql", MysqlDataSourceLoader.INSTANCE);
        registerDatabaseLoader("sqlite", SqliteDataSourceLoader.INSTANCE);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        switch (lifecyclePhase) {
            case ACTIVE -> {
                databaseConnection = loadDatabaseConnection();
            }
            case RELOAD -> {
                if (databaseConnection != null) {
                    databaseConnection.closeQuietly();
                }
                DaoManager.clearCache();
                databaseConnection = loadDatabaseConnection();
            }
            case DISABLE -> {
                if (databaseConnection != null) {
                    databaseConnection.closeQuietly();
                }
            }
        }
    }

    private ConnectionSource loadDatabaseConnection() {
        String databaseType = DatabaseConfigs.TYPE.value().toLowerCase();
        DataSourceLoader dataSourceLoader = databaseLoaderMap.get(databaseType);
        if (dataSourceLoader == null) {
            throw new DatabaseLoadException("Unknown database type: " + databaseType);
        }
        try {
            return dataSourceLoader.load();
        } catch (SQLException e) {
            throw new DatabaseLoadException(e);
        }
    }

    public void registerDatabaseLoader(String type, DataSourceLoader dataSourceLoader) {
        databaseLoaderMap.put(type, dataSourceLoader);
    }

    public ConnectionSource databaseConnection() {
        // 第一次检查（无锁）
        if (databaseConnection == null || !databaseConnection.isOpen(null)) {
            synchronized (this) {
                // 第二次检查（锁内）
                if (databaseConnection == null || !databaseConnection.isOpen(null)) {
                    // 如果存在旧连接（已关闭），先释放资源
                    if (databaseConnection != null) {
                        databaseConnection.closeQuietly();
                    }
                    databaseConnection = loadDatabaseConnection();
                }
            }
        }
        return databaseConnection;
    }

}
