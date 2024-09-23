package xyz.f2reninj5.smpwarp.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.f2reninj5.smpwarp.model.Warp;
import xyz.f2reninj5.smpwarp.model.WarpIdentifier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpDatabase {

    private final Connection connection;

    public WarpDatabase(@NotNull String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS warp (`name` TEXT NOT NULL,
                `group` TEXT DEFAULT "" NOT NULL,
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

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void createWarp(@NotNull WarpIdentifier identifier, @NotNull Location location, @NotNull Player createdBy) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
            INSERT INTO warp (`group`, `name`, `world`, `x`, `y`, `z`, `yaw`, `pitch`, `created_by`)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """);
        statement.setString(1, identifier.getGroup());
        statement.setString(2, identifier.getName());
        statement.setString(3, location.getWorld().getUID().toString());
        statement.setDouble(4, location.getX());
        statement.setDouble(5, location.getY());
        statement.setDouble(6, location.getZ());
        statement.setDouble(7, location.getYaw());
        statement.setDouble(8, location.getPitch());
        statement.setString(9, createdBy.getUniqueId().toString());
        statement.executeUpdate();
    }

    public Warp getWarp(@NotNull WarpIdentifier identifier) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
            SELECT * FROM warp WHERE `group` = ? AND `name` = ?
        """);
        statement.setString(1, identifier.getGroup());
        statement.setString(2, identifier.getName());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Warp(
                new WarpIdentifier(
                    resultSet.getString("group"),
                    resultSet.getString("name")
                ),
                new Location(
                    Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))),
                    resultSet.getDouble("x"),
                    resultSet.getDouble("y"),
                    resultSet.getDouble("z"),
                    resultSet.getFloat("yaw"),
                    resultSet.getFloat("pitch")
                ),
                Bukkit.getPlayer(UUID.fromString(resultSet.getString("created_by")))
            );
        } else {
            return null;
        }
    }

    public List<String> getAllWarpGroups() throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("""
            SELECT `group` FROM warp WHERE `group` != ""
        """);

        List<String> groups = new ArrayList<>();

        while (resultSet.next()) {
            groups.add(resultSet.getString("group"));
        }
        return groups;
    }

    public List<String> getWarpGroups(@NotNull String filter) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
           SELECT `group` FROM warp WHERE `group` LIKE ? AND `group` != ""
        """);
        statement.setString(1, filter + "%");
        ResultSet resultSet = statement.executeQuery();

        List<String> groups = new ArrayList<>();

        while (resultSet.next()) {
            groups.add(resultSet.getString("group"));
        }
        return groups;
    }

    public List<String> getWarpNames(@NotNull String group, @NotNull String filter) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("""
            SELECT `name` FROM warp WHERE `group` = ? AND `name` LIKE ?
        """);
        statement.setString(1, group);
        statement.setString(2, filter + "%");
        ResultSet resultSet = statement.executeQuery();

        List<String> names = new ArrayList<>();

        while (resultSet.next()) {
            names.add(resultSet.getString("name"));
        }
        return names;
    }

    public boolean warpExists(@NotNull WarpIdentifier identifier) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT COUNT(*) FROM warp WHERE `group` = ? AND `name` = ?
        """)) {
            statement.setString(1, identifier.getGroup());
            statement.setString(2, identifier.getName());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.getInt(1) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<Warp> getAllWarps() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM warp");
            List<Warp> warps = new ArrayList<>();
            while (resultSet.next()) {
                warps.add(new Warp(
                    new WarpIdentifier(
                        resultSet.getString("group"),
                        resultSet.getString("name")
                    ),
                    new Location(
                        Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch")
                    ),
                    Bukkit.getPlayer(UUID.fromString(resultSet.getString("created_by")))
                ));
            }
            return warps;
        }
    }

    public void removeWarp(@NotNull WarpIdentifier identifier) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            DELETE FROM warp WHERE `group` = ? AND `name` = ?
        """)) {
            statement.setString(1, identifier.getGroup());
            statement.setString(2, identifier.getName());
            statement.executeUpdate();
        }
    }

    public void moveWarp(@NotNull WarpIdentifier identifier, @NotNull Location location) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            UPDATE warp
            SET x = ?,
                y = ?,
                z = ?,
                yaw = ?,
                pitch = ?,
                world = ?
            WHERE `group` = ? AND `name` = ?
        """)) {
            statement.setDouble(1, location.getX());
            statement.setDouble(2, location.getY());
            statement.setDouble(3, location.getZ());
            statement.setFloat(4, location.getYaw());
            statement.setFloat(5, location.getPitch());
            statement.setString(6, location.getWorld().getUID().toString());
            statement.setString(7, identifier.getGroup());
            statement.setString(8, identifier.getName());
            statement.executeUpdate();
        }
    }

    public void renameWarp(@NotNull WarpIdentifier oldIdentifier, @NotNull WarpIdentifier newIdentifier) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("""
            UPDATE warp SET `group` = ?, `name` = ? WHERE `group` = ? AND `name` = ?
        """)) {
            statement.setString(1, newIdentifier.getGroup());
            statement.setString(2, newIdentifier.getName());
            statement.setString(3, oldIdentifier.getGroup());
            statement.setString(4, oldIdentifier.getName());
            statement.executeUpdate();
        }
    }
}
