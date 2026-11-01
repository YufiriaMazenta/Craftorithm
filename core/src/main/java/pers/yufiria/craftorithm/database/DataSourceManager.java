package pers.yufiria.craftorithm.database;

import java.util.Locale;
import crypticlib.CrypticLibPlugin;
import crypticlib.database.connection.ConnectionSource;
import crypticlib.database.dao.DaoManager;
import crypticlib.lifecycle.LifecyclePhase;
import crypticlib.lifecycle.LifecycleSchedule;
import crypticlib.lifecycle.LifecycleTask;
import crypticlib.lifecycle.LifecycleTaskConfig;
import pers.yufiria.craftorithm.config.DatabaseConfigs;
import pers.yufiria.craftorithm.database.exception.DatabaseLoadException;
import pers.yufiria.craftorithm.database.loader.DataSourceLoader;
import pers.yufiria.craftorithm.database.loader.MysqlDataSourceLoader;
import pers.yufiria.craftorithm.database.loader.PostgresqlDataSourceLoader;
import pers.yufiria.craftorithm.database.loader.SqliteDataSourceLoader;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@LifecycleTaskConfig(
    schedules = {
        @LifecycleSchedule(phase = LifecyclePhase.RELOAD, isAsync = true, priority = -1),
        @LifecycleSchedule(phase = LifecyclePhase.DISABLE, priority = Integer.MAX_VALUE)
    }
)
public enum DataSourceManager implements LifecycleTask {

    INSTANCE;
    private final Map<String, DataSourceLoader> databaseLoaderMap = new ConcurrentHashMap<>();
    private volatile ConnectionSource databaseConnection;

    DataSourceManager() {
        registerDatabaseLoader("mysql", MysqlDataSourceLoader.INSTANCE);
        registerDatabaseLoader("sqlite", SqliteDataSourceLoader.INSTANCE);
        registerDatabaseLoader("postgresql", PostgresqlDataSourceLoader.INSTANCE);
    }

    @Override
    public void onLifecycle(CrypticLibPlugin crypticLibPlugin, LifecyclePhase lifecyclePhase) {
        switch (lifecyclePhase) {
            case RELOAD -> {
                if (databaseConnection != null) {
                    databaseConnection.close();
                }
                DaoManager.clearCache();
                databaseConnection = loadDatabaseConnection();
            }
            case DISABLE -> {
                if (databaseConnection != null) {
                    databaseConnection.close();
                }
                databaseLoaderMap.clear();
            }
        }
    }

    private ConnectionSource loadDatabaseConnection() {
        String databaseType = DatabaseConfigs.TYPE.value().toLowerCase(Locale.ROOT);
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
        ConnectionSource current = databaseConnection;
        //外层也要判 isOpen: 否则重建窗口内(旧连接已close、新连接未赋值)会直接把已关闭的连接返回给调用方
        if (current != null && current.isOpen()) {
            return current;
        }
        synchronized (this) {
            current = databaseConnection;
            if (current == null || !current.isOpen()) {
                if (current != null) {
                    current.close();
                }
                //先置空再重建: 重建失败时字段保持null, 下次调用可以重试, 不会一直返回已关闭的连接
                databaseConnection = null;
                databaseConnection = loadDatabaseConnection();
            }
            return databaseConnection;
        }
    }

}
