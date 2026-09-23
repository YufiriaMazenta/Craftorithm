package pers.yufiria.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

@ConfigHandler(path = "database.yml")
public class DatabaseConfigs {

    public static final StringConfig TYPE = new StringConfig(
        "type",
        "sqlite",
        "使用什么类型的数据库, 支持sqlite、mysql和postgresql"
    );
    public static final ConfigSectionConfig SQLITE = new ConfigSectionConfig(
        "sqlite"
    );
    public static final ConfigSectionConfig MYSQL = new ConfigSectionConfig(
        "mysql"
    );
    public static final ConfigSectionConfig POSTGRESQL = new ConfigSectionConfig(
        "postgresql",
        () -> {
            ConfigurationSection def = new MemoryConfiguration();
            def.set("host", "localhost");
            def.set("port", 5432);
            def.set("database", "craftorithm");
            def.set("username", "postgres");
            def.set("password", "your_password");
            def.set("parameters", "sslmode=disable");
            def.set("pool.max_connections", 8);
            def.set("pool.max_idle_time_ms", 60000);
            def.set("pool.max_lifetime_ms", 1800000);
            def.set("pool.check_connections_every_ms", 5000);
            def.set("pool.test_before_get", true);
            return def;
        }
    );

}
