package com.blackjack.database;

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

    public void addUser(String user_name, String user_password) throws SQLException {
        if (connection == null) {
            return;
        }

        String sql = String.format("INSERT INTO blackjack_user VALUES('%s', '%s');", user_name, user_password);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error during user registration:");
            e.printStackTrace();
            throw e;
        }
    }
    
    public boolean checkUsernameExists(String user_name) {
        String sql = String.format("SELECT user_password FROM blackjack_user WHERE user_name = '%S';", user_name);

        return true;
    }
    
    public boolean checkLoginData(String user_name, String user_password) throws SQLException {

        if (connection == null) {
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
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        return false;
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
