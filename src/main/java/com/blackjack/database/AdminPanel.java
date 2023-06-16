package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        Button banButton = new Button("Ban User");
        banButton.addClassName("red-button");
        banButton.addClickListener(event -> {
            Player selectedPlayer = getPlayerGrid().asSingleSelect().getValue();
            if (selectedPlayer != null) {
                selectedPlayer.setBanned(true);
                getPlayerGrid().getDataProvider().refreshAll();
                updateBannedStatus(selectedPlayer.getPlayerName(), true);
            }
        });

        Button backButton= new Button("Back to Login");
        backButton.addClassName("grey-button");
        backButton.addClickListener(event -> {
            UI.getCurrent().navigate("login");
        });


        Button unbanButton = new Button("Unban User");
        unbanButton.addClassName("green-button");
        unbanButton.addClickListener(event -> {
            Player selectedPlayer = getPlayerGrid().asSingleSelect().getValue();
            if (selectedPlayer != null) {
                selectedPlayer.setBanned(false);
                getPlayerGrid().getDataProvider().refreshAll();
                updateBannedStatus(selectedPlayer.getPlayerName(), false);
            }
        });

        HorizontalLayout buttonLayout= new HorizontalLayout();
        buttonLayout.add(banButton,unbanButton,backButton);

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

    private Grid<Player> getPlayerGrid() {
        return playerGrid;
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
                    Player player = new Player(userName, isBanned);
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
}
