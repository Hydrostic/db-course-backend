package org.LunaTelecom.infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.LunaTelecom.config.Config;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.mariadb.jdbc.MariaDbDataSource;
import org.tinylog.Logger;

import java.sql.SQLException;

public class Database {
    public static Jdbi jdbi;
    public static void configure (Config.DBConfig config) throws SQLException {
        MariaDbDataSource ds;
        try {
            ds = new MariaDbDataSource();
            ds.setUrl(String.format("jdbc:mariadb://%s:%d/%s",
                    config.host,
                    config.port,
                    config.database));
            ds.setUser(config.user);
            ds.setPassword(config.password);
        } catch (SQLException e) {
            Logger.error("Maybe invalid database configuration", e);
            throw e;
        }
        HikariConfig hc = new HikariConfig();
        hc.setDataSource(ds);
        hc.setMaximumPoolSize(8);
        jdbi = Jdbi.create(new HikariDataSource(hc));
        jdbi.installPlugin(new SqlObjectPlugin());
    }

    public static String getVersion() {
        return jdbi.withHandle(handle ->
            handle.createQuery("select version()").mapTo(String.class).first()
        );
    }
}
