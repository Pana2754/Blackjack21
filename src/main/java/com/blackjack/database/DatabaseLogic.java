package com.blackjack.database;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

public class DatabaseLogic {
    public static void connectToDb(String user_name, String user_password) throws ClassNotFoundException {

        String connectionUrl = "jdbc:sqlserver://provadis-it-ausbildung.de:1433;"
        		+ "databaseName=BlackJack01;"
        		+ "user=BlackJackUser01;"
        		+ "password=ProvadisBlackJackUser01&/";
        
        
        try {
            // Load SQL Server JDBC driver and establish connection.
            System.out.print("Connecting to SQL Server ... ");
            try (Connection connection = DriverManager.getConnection(connectionUrl)) {
                System.out.println("Done.");

                // Create a sample database
                String sql = "INSERT INTO blackjack_user VALUES ('test', 'test');";
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sql);
                    System.out.println("Done.");
                }
            } catch (Exception e) {
                System.out.println();
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}