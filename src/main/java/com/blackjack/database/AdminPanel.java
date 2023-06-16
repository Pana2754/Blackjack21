package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Admin Panel")
@Route("admin-panel")
public class AdminPanel extends VerticalLayout {
    private List<Player> players = new ArrayList<>();
    private Grid<Player> playerGrid;

    public AdminPanel() {
        setWidthFull();
        addClassName("admin-panel-title");
        H2 title = new H2("Adminpanel");

        Button backButton = new Button("Back to Login");
        backButton.addClassName("grey-button");
        backButton.addClickListener(event -> {
            UI.getCurrent().navigate("login");
        });

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(backButton);

        playerGrid = createPlayerGrid();
        fetchAllUsers();
        playerGrid.setItems(players);

        add(title, playerGrid, buttonLayout);
        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        } else if (!isAnAdmin()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }
    }

    private Grid<Player> createPlayerGrid() {
        Grid<Player> grid = new Grid<>();
        grid.addColumn(Player::getPlayerName).setHeader("Name");
        grid.addColumn(Player::isBanned).setHeader("Banned");
        grid.addColumn(Player::getBalance).setHeader("Balance");

        // Add new column with Ban and Unban buttons
        grid.addComponentColumn(player -> {
            Button banButton = new Button("Ban");
            banButton.addClickListener(event -> {
                Notification.show("Banned: " + player.getPlayerName());
                player.setBanned(true);
                updateBannedStatus(player.getPlayerName(), true);
                grid.getDataProvider().refreshItem(player);
            });

            Button unbanButton = new Button("Unban");
            unbanButton.addClickListener(event -> {
                Notification.show("Unbanned: " + player.getPlayerName());
                player.setBanned(false);
                updateBannedStatus(player.getPlayerName(), false);
                grid.getDataProvider().refreshItem(player);
            });

            HorizontalLayout buttonsLayout = new HorizontalLayout(banButton, unbanButton);
            return buttonsLayout;
        }).setHeader("Actions");

        // Add new column with TextField and Button to set balance
        grid.addComponentColumn(player -> {
            TextField balanceField = new TextField();
            balanceField.setPlaceholder("New balance");

            Button setBalanceButton = new Button("Set Balance");
            setBalanceButton.addClickListener(event -> {
                try {
                    float newBalance = Float.parseFloat(balanceField.getValue());
                    player.setBalance(newBalance);
                    updateBalance(player.getPlayerName(), newBalance);
                    Notification.show("Balance updated for: " + player.getPlayerName());
                    grid.getDataProvider().refreshItem(player);
                } catch (NumberFormatException e) {
                    Notification.show("Invalid balance input");
                }
            });

            HorizontalLayout balanceLayout = new HorizontalLayout(balanceField, setBalanceButton);
            return balanceLayout;
        }).setHeader("Set Balance");

        return grid;
    }

    private boolean isAnAdmin() {
        Player player = getActivePlayer();
        return player != null && player.getPlayerName().equals("admin");
    }

    private boolean isLoggedIn() {
        return getActivePlayer() != null;
    }

    private Player getActivePlayer() {
        return (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
    }

    private void fetchAllUsers() {
        try {
            DatabaseLogic dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            Connection connection = dbLogic.getConnection();
            String sql = "SELECT * FROM blackjack_user";
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    String userName = result.getString("user_name");
                    boolean isBanned = result.getBoolean("isBanned");
                    double balance = result.getDouble("balance");
                    Player player = new Player(userName, isBanned, balance);
                    players.add(player);
                }
            }
            dbLogic.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBannedStatus(String playerName, boolean isBanned) {
        try {
            DatabaseLogic dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            Connection connection = dbLogic.getConnection();
            String sql = "UPDATE blackjack_user SET isBanned = ? WHERE user_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isBanned);
                statement.setString(2, playerName);
                statement.executeUpdate();
            }
            dbLogic.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBalance(String playerName, double newBalance) {
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
}
