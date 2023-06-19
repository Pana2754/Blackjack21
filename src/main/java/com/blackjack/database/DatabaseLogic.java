package com.blackjack.database;

import com.vaadin.flow.component.notification.Notification;

import java.sql.*;

public class DatabaseLogic {

    private Connection connection;

    public void connectToDb() throws SQLException {
        String connectionUrl = "jdbc:sqlserver://provadis-it-ausbildung.de:1433;"
                + "databaseName=BlackJack01;"
                + "user=BlackJackUser01;"
                + "password=ProvadisBlackJackUser01&/";

        try {
            System.out.print("Connecting to SQL Server ... ");
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println("Done.");
        } catch (SQLException e) {
            System.out.println();
            e.printStackTrace();
            throw e;
        }
    }
    public void updateBalance(String playerName, double newBalance) {
        try {
            DatabaseLogic dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            Connection connection = dbLogic.getConnection();
            String sql = "UPDATE blackjack_user SET balance = ? WHERE user_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, newBalance);
                statement.setString(2, playerName);
                statement.executeUpdate();
            }
            dbLogic.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void addUser(String user_name, String user_password, boolean isAdmin, boolean isBanned, float balance) throws SQLException {
        if (connection == null) {
            return;
        }

        String sql = String.format("INSERT INTO blackjack_user VALUES('%s', '%s', '%s', '%s', '%s');", user_name, user_password, isAdmin, isBanned, balance);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error during user registration:");
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkLoginData(String user_name, String user_password) throws SQLException {
        if (connection == null) {
            return false;
        }
        String sql = String.format("SELECT user_password, isBanned FROM blackjack_user WHERE user_name = '%S';", user_name);
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) {
                String db_password = result.getString("user_password");
                boolean isBanned = result.getBoolean("isBanned");

                if (isBanned) {
                    Notification.show("You are banned!");
                } else if (db_password.equals(user_password)) {
                    return true;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }


    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public boolean doesUserExist(String user_name) throws SQLException {
        if (connection == null) {
            return false;
        }

        String sql = String.format("SELECT user_name FROM blackjack_user WHERE user_name = '%s';", user_name);
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error during checking user existence:");
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    public  Player getUser(String username) {
        Connection connection = null;
        try {
            DatabaseLogic dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            connection = dbLogic.getConnection();
            String sql = "SELECT * FROM blackjack_user WHERE user_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        String userName = result.getString("user_name");
                        boolean isBanned = result.getBoolean("isBanned");
                        double balance = result.getDouble("balance");

                        return new Player(userName, false, balance,isBanned);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public boolean checkAdmin(String user_name) {
        if (connection == null) {
            return false;
        }
        String sql = String.format("SELECT is_Admin FROM blackjack_user WHERE user_name = '%S';", user_name);
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) {
                String isAdmin = result.getString("is_Admin");
                if (isAdmin.equals("1")) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }
}
