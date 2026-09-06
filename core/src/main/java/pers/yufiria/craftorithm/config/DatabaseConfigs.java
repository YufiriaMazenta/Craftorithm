package pers.yufiria.craftorithm.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.node.impl.bukkit.ConfigSectionConfig;
import crypticlib.config.node.impl.bukkit.IntConfig;
import crypticlib.config.node.impl.bukkit.StringConfig;

@ConfigHandler(path = "database.yml")
public class DatabaseConfigs {

    public static final StringConfig TYPE = new StringConfig(
        "type",
        "sqlite",
        "使用什么类型的数据库, 支持sqlite和mysql"
    );
    public static final ConfigSectionConfig SQLITE = new ConfigSectionConfig(
        "sqlite"
    );
    public static final ConfigSectionConfig MYSQL = new ConfigSectionConfig(
        "mysql"
    );

}
