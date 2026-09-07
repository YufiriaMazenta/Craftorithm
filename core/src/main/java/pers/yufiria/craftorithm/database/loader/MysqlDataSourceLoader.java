package pers.yufiria.craftorithm.database.loader;

import crypticlib.database.connection.ConnectionSource;
import crypticlib.database.connection.PooledConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.config.DatabaseConfigs;

import java.sql.SQLException;

public enum MysqlDataSourceLoader implements DataSourceLoader {

    INSTANCE;

    @Override
    public ConnectionSource load() throws SQLException {
        ConfigurationSection mysqlConfig = DatabaseConfigs.MYSQL.value();
        String host = mysqlConfig.getString("host", "localhost");
        int port = mysqlConfig.getInt("port", 3306);
        String database = mysqlConfig.getString("database", "craftorithm");
        String params = mysqlConfig.getString("parameters");
        String username = mysqlConfig.getString("username", "root");
        String password = mysqlConfig.getString("password", "");
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        if (params != null && !params.isEmpty()) {
            jdbcUrl += "?" + params;
        }
        ConfigurationSection poolConfig = mysqlConfig.getConfigurationSection("pool");
        int maxConnections = poolConfig != null ? poolConfig.getInt("max_connections", 10) : 10;
        long maxIdleTimeMs = poolConfig != null ? poolConfig.getLong("max_idle_time_ms", 60000) : 60000;
        long maxLifetimeMs = poolConfig != null ? poolConfig.getLong("max_lifetime_ms", 1800000) : 1800000;
        long checkConnectionsEveryMs = poolConfig != null ? poolConfig.getLong("check_connections_every_ms", 5000) : 5000;
        boolean testBeforeGet = poolConfig != null && poolConfig.getBoolean("test_before_get", true);
        PooledConnectionSource connectionSource = new PooledConnectionSource(jdbcUrl, username, password);
        connectionSource.setMaxConnections(maxConnections);
        connectionSource.setMaxIdleTimeMs(maxIdleTimeMs);
        connectionSource.setMaxLifetimeMs(maxLifetimeMs);
        connectionSource.setCheckConnectionsEveryMs(checkConnectionsEveryMs);
        connectionSource.setTestBeforeGet(testBeforeGet);
        return connectionSource;
    }

}
