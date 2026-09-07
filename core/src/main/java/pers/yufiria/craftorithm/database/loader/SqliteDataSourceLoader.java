package pers.yufiria.craftorithm.database.loader;

import crypticlib.database.connection.JdbcConnectionSource;
import crypticlib.database.connection.ConnectionSource;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.craftorithm.config.DatabaseConfigs;

import java.sql.SQLException;

public enum SqliteDataSourceLoader implements DataSourceLoader {

    INSTANCE;

    @Override
    public ConnectionSource load() throws SQLException {
        ConfigurationSection databaseConfig = DatabaseConfigs.SQLITE.value();
        String file = databaseConfig.getString("file", "plugins/Craftorithm/data.db");
        String params = databaseConfig.getString("parameters");
        String jdbcUrl = "jdbc:sqlite:" + file;
        if (params != null && !params.isEmpty()) {
            jdbcUrl += "?" + params;
        }
        return new JdbcConnectionSource(jdbcUrl);
    }

}
