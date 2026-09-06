package pers.yufiria.craftorithm.database.loader;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.config.DatabaseConfigs;

import java.sql.SQLException;

public enum MysqlDataSourceLoader implements DataSourceLoader {

    INSTANCE;

    @Override
    public ConnectionSource load() throws SQLException {
        ConfigurationSection databaseConfig = DatabaseConfigs.MYSQL.value();
        String host = databaseConfig.getString("host", "localhost");
        int port = databaseConfig.getInt("port", 3306);
        String database = databaseConfig.getString("database", "craftorithm");
        String params = databaseConfig.getString("parameters");
        String username = databaseConfig.getString("username", "root");
        String password = databaseConfig.getString("password", "");
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
        if (params != null && !params.isEmpty()) {
            jdbcUrl += "?" + params;
        }
        int checkConnectionEveryMillis = databaseConfig.getInt("check_connection_every_millis", 5000);
        long maxConnectionAgeMillis = databaseConfig.getLong("max_connection_age_millis", 1800000);
        int maxConnectionFree = databaseConfig.getInt("max_connection_free", 10);
        boolean testBeforeGet = databaseConfig.getBoolean("test_before_get", true);
        JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource(jdbcUrl, username, password);
        connectionSource.setCheckConnectionsEveryMillis(checkConnectionEveryMillis);
        connectionSource.setMaxConnectionsFree(maxConnectionFree);
        connectionSource.setMaxConnectionAgeMillis(maxConnectionAgeMillis);
        connectionSource.setTestBeforeGet(testBeforeGet);
        connectionSource.initialize();
        return connectionSource;
    }

}
