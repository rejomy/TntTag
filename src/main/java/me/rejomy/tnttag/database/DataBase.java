package me.rejomy.tnttag.database;

import me.rejomy.tnttag.data.DataManager;
import me.rejomy.tnttag.data.PlayerData;

import java.sql.*;
import java.util.UUID;

public abstract class DataBase {
    protected abstract Connection getConnection() throws SQLException;
    public Connection connection;

    public void set(UUID uuid, int wins, int games, int rounds, double kd, long time) throws SQLException {
        ResultSet set = null;

        try {
            set = executeQuery("INSERT OR REPLACE INTO users VALUES ('" + uuid + "', '" + wins + "', '" + games + "', '" +
                    rounds + "', '" + kd + "', " + time + "')");
        } finally {
            if (set != null) {
                set.close();
                set.getStatement().close();
            }
        }
    }

    public String get(UUID uuid, int column) {
        try {
            ResultSet set = executeQuery("SELECT * FROM users WHERE uuid='" + uuid + "'");
            String value = set.getString(column);
            set.close();
            return value;
        } catch (SQLException | NullPointerException exception) {}

        return "";
    }

    public void remove(UUID uuid) throws SQLException {
        ResultSet set = null;
        try {
            set = executeQuery("DELETE FROM users WHERE uuid='" + uuid + "'");
        } finally {
            if (set != null) {
                set.close();
            }
        }
    }

    private ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet set = null;
        try {
             set = statement.executeQuery(query);
        } catch (SQLException exception) {}

        return set;
    }

    public void loadDataFromDataBase() throws SQLException {
        Statement statement = connection.createStatement();
        // Выполняем запрос, который выбирает все записи из таблицы users
        ResultSet resultSet = executeQuery("SELECT * FROM users");

        // Перебираем все записи в result set
        while (resultSet.next()) {
            long time = Long.parseLong(resultSet.getString("changeTime"));
            UUID uuid = UUID.fromString(resultSet.getString("uuid"));

            if(System.currentTimeMillis() - time > 14 * 24 * 60 * 60 * 1000) {
                remove(uuid);
                continue;
            }

            // Создаем новый объект PlayerData
            PlayerData playerData = new PlayerData();

            // Заполняем его поля значениями из result set
            playerData.uuid = UUID.fromString(resultSet.getString("uuid"));
            playerData.rounds = resultSet.getInt("rounds");
            playerData.wins = resultSet.getInt("wins");
            playerData.games = resultSet.getInt("games");
            playerData.winsAndLoses = resultSet.getDouble("kd");

            // Добавляем объект в список
            DataManager.add(playerData);
        }

        // Закрываем statement и resultSet
        statement.close();
        resultSet.close();
    }

    public void savePlayersData() throws SQLException {
        // Перебираем все элементы списка playersData
        for (PlayerData playerData : DataManager.USERS) {
            set(playerData.uuid,
                    playerData.wins, playerData.games, playerData.rounds, playerData.winsAndLoses, System.currentTimeMillis());
        }
    }
}
