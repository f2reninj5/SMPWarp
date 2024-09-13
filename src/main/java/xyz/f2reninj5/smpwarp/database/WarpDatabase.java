package xyz.f2reninj5.smpwarp.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.f2reninj5.smpwarp.model.Warp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public Warp getWarp(String name, String group) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT * FROM warp WHERE `name` = ? AND `group` = ?
        """)) {
            statement.setString(1, name);
            statement.setString(2, group);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Warp(
                    resultSet.getString("name"),
                    resultSet.getString("group"),
                    new Location(
                        Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch")
                    ),
                    resultSet.getString("created_by")
                );
            } else {
                return null;
            }
        }
    }

    public List<String> getWarpGroups() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("""
                SELECT `group` FROM warp
            """);
            List<String> groups = new ArrayList<String>();
            while (resultSet.next()) {
                groups.add(resultSet.getString("group"));
            }
            return groups;
        }
    }

    public List<String> getWarpGroups(String filter) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
           SELECT `group` FROM warp WHERE `group` LIKE `?%`
        """)) {
            statement.setString(1, filter);
            ResultSet resultSet = statement.executeQuery();
            List<String> groups = new ArrayList<String>();
            while (resultSet.next()) {
                groups.add(resultSet.getString("group"));
            }
            return groups;
        }
    }
}
