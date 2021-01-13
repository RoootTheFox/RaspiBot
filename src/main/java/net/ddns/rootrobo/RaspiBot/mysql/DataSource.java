package net.ddns.rootrobo.RaspiBot.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.ddns.rootrobo.RaspiBot.Main;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.addDataSourceProperty("serverName", Main.MYSQL_HOST);
        config.addDataSourceProperty("databaseName", Main.MYSQL_DB);
        config.addDataSourceProperty("port", 3306);
        config.setUsername(Main.MYSQL_USER);
        config.setPassword(Main.MYSQL_PASS);
        config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.setPoolName("raspibot");
        ds = new HikariDataSource( config );
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();

    }
}