package pers.yufiria.craftorithm.database.loader;

import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public interface DataSourceLoader {

    ConnectionSource load() throws SQLException;

}
