package pers.yufiria.craftorithm.database.loader;

import crypticlib.database.connection.ConnectionSource;
import crypticlib.database.connection.PooledConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.config.DatabaseConfigs;

import java.sql.SQLException;

public enum PostgresqlDataSourceLoader implements DataSourceLoader {

    INSTANCE;

    @Override
    public ConnectionSource load() throws SQLException {
        //驱动由Paper按plugin.yml的libraries在运行时下载到插件类加载器,
        //这里显式加载驱动类, 保证它注册进DriverManager而不依赖线程上下文类加载器
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC driver not found", e);
        }
        ConfigurationSection postgresqlConfig = DatabaseConfigs.POSTGRESQL.value();
        String host = postgresqlConfig.getString("host", "localhost");
        int port = postgresqlConfig.getInt("port", 5432);
        String database = postgresqlConfig.getString("database", "craftorithm");
        String params = postgresqlConfig.getString("parameters");
        String username = postgresqlConfig.getString("username", "postgres");
        String password = postgresqlConfig.getString("password", "");
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        if (params != null && !params.isEmpty()) {
            jdbcUrl += "?" + params;
        }
        ConfigurationSection poolConfig = postgresqlConfig.getConfigurationSection("pool");
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