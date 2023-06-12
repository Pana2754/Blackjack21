package com.blackjack.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseLogic {

    private Connection connection;

    public void connectToDb() throws ClassNotFoundException, SQLException {

        String connectionUrl = "jdbc:sqlserver://provadis-it-ausbildung.de:1433;"
                + "databaseName=BlackJack01;"
                + "user=BlackJackUser01;"
                + "password=ProvadisBlackJackUser01&/";

        try {
            // Load SQL Server JDBC driver and establish connection.
            System.out.print("Connecting to SQL Server ... ");
            connection = DriverManager.getConnection(connectionUrl);
            System.out.println("Done.");
        } catch (SQLException e) {
            System.out.println();
            e.printStackTrace();
            throw e;
        }
    }

    public void addUser(String user_name, String user_password) throws SQLException {

        if (connection == null){
            /*error*/
            return;
        }
        String sql = String.format("INSERT INTO blackjack_user VALUES('%s', '%s');", user_name, user_password);
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(sql);
            //throw ERROR
        }

    }

    public void checkLoginData(String user_name, String user_password) throws SQLException {

        if (connection == null) {
            /*error*/
            return;
        }
        String sql = String.format("SELECT EXISTS * FROM blackjack_user WHERE user_name = '%s' AND password = '%s';", user_name, user_password);
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate(sql); // vergleich muss noch stattfinden
            //throw ERROR
        }
    }

    // When you are done with the connection, it's a good practice to close it.
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
