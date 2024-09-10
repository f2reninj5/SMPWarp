package xyz.f2reninj5.smpwarp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class WarpDatabase {

    private final Connection connection;

    public WarpDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
            CREATE TABLE IF NOT EXISTS warp (`name` TEXT NOT NULL,
                `group` TEXT NOT NULL,
                `world` TEXT NOT NULL,
                `x` REAL NOT NULL,
                `y` REAL NOT NULL,
                `z` REAL NOT NULL,
                `yaw` REAL NOT NULL,
                `pitc` REAL NOT NULL,
                `created_by` TEXT NOT NULL,
                PRIMARY KEY (`name`, `group`)
            )
            """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
