package xyz.f2reninj5.smpwarp.database;

import org.bukkit.Location;

import java.sql.*;

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
                `pitch` REAL NOT NULL,
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

    public void createWarp(String name, String group, Location location, String createdBy) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            INSERT INTO warp (`name`, `group`, `world`, `x`, `y`, `z`, `yaw`, `pitch`, `created_by`)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """)) {
            statement.setString(1, name);
            statement.setString(2, group);
            statement.setString(3, location.getWorld().getUID().toString());
            statement.setDouble(4, location.getX());
            statement.setDouble(5, location.getY());
            statement.setDouble(6, location.getZ());
            statement.setDouble(7, location.getYaw());
            statement.setDouble(8, location.getPitch());
            statement.setString(9, createdBy);
            statement.executeUpdate();
        }
    }
}
