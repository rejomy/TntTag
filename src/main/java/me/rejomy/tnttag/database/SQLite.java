package me.rejomy.tnttag.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends DataBase {

    public SQLite() {

        try {
            Class.forName("org.sqlite.JDBC").newInstance();

            connection = getConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (uuid TEXT PRIMARY KEY, " +
                    "rounds INT, " +
                    "games INT, " +
                    "kd DOUBLE, " +
                    "wins INT, " +
                    "changeTime LONG)");

            statement.close();
        } catch (Exception ignored) {}

    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:plugins/TntTag/users.db");
    }

}
