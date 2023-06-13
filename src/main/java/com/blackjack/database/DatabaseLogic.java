package com.blackjack.database;

import java.sql.*;

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

    public boolean checkLoginData(String user_name, String user_password) throws SQLException {

        if (connection == null) {
            /*error*/
            return false;
        }
        String sql = String.format("SELECT user_password FROM blackjack_user WHERE user_name = '%S';", user_name);
        try (Statement statement = connection.createStatement()){
            ResultSet result = statement.executeQuery(sql);

            if(result.next()){

                String db_password = result.getString("user_password");

                if (db_password.equals(user_password)){
                    return true;
                }
            }

            // vergleich muss noch stattfinden
            //throw ERROR
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        return false;
    }

    // When you are done with the connection, it's a good practice to close it.
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
