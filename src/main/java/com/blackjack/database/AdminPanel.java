package com.blackjack.database;

import com.vaadin.flow.component.AttachEvent;
        import com.vaadin.flow.component.UI;
        import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
        import com.vaadin.flow.component.html.H2;
        import com.vaadin.flow.component.notification.Notification;
        import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
        import com.vaadin.flow.component.orderedlayout.VerticalLayout;
        import com.vaadin.flow.component.textfield.TextField;
        import com.vaadin.flow.router.PageTitle;
        import com.vaadin.flow.router.Route;
        import com.vaadin.flow.server.VaadinSession;

        import java.sql.Connection;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.util.ArrayList;
        import java.util.List;

@PageTitle("Admin Panel")
@Route("admin-panel")
@CssImport("./themes/mytodo/adminview.css")
public class AdminPanel extends VerticalLayout {
    private final List<Player> players = new ArrayList<>();
    private final Grid<Player> playerGrid;

    public AdminPanel() {
        setWidthFull();
        addClassName("admin-panel-title");
        H2 title = new H2("ADMINPANEL");

        Button backButton = new Button("Back to Login");
        backButton.addClassName("grey-button");
        backButton.addClickListener(event -> UI.getCurrent().navigate("login"));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(backButton);

        playerGrid = createPlayerGrid();
        add(title, playerGrid, buttonLayout);
        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
        } else if (!isAnAdmin()) {
            UI.getCurrent().navigate(LoginView.class);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        try {
            updateGridData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateGridData() throws SQLException {
        players.clear();
        fetchAllUsers();
        playerGrid.setItems(players);
    }

    private Grid<Player> createPlayerGrid() {
        Grid<Player> grid = new Grid<>();
        grid.getStyle().set("opacity", "0.75");

        grid.addColumn(Player::getPlayerName).setHeader("Name");
        grid.addColumn(Player::getBalance).setHeader("Balance");

        grid.addComponentColumn(player -> {
            Button banButton = new Button("Ban");
            Button unbanButton = new Button("Unban");

            banButton.getStyle().set("color", player.isBanned() ? "initial" : "red");
            unbanButton.getStyle().set("color", player.isBanned() ? "green" : "initial");

            banButton.addClickListener(event -> {
                Notification.show("Banned: " + player.getPlayerName());
                player.setBanned(true);
                try {
                    updateBannedStatus(player.getPlayerName(), true);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                grid.getDataProvider().refreshItem(player);
                banButton.getStyle().set("color", "initial");
                unbanButton.getStyle().set("color", "green");
            });

            unbanButton.addClickListener(event -> {
                Notification.show("Unbanned: " + player.getPlayerName());
                player.setBanned(false);
                try {
                    updateBannedStatus(player.getPlayerName(), false);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                grid.getDataProvider().refreshItem(player);
                unbanButton.getStyle().set("color", "initial");
                banButton.getStyle().set("color", "red");
            });

            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(event -> {
                try {
                    delete(player.getPlayerName());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Notification.show("Deleted: " + player.getPlayerName());
                try {
                    updateGridData();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            return new HorizontalLayout(banButton, unbanButton, deleteButton);
        }).setHeader("Actions");

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
                    grid.getDataProvider().refreshAll();
                } catch (NumberFormatException | SQLException e) {
                    Notification.show("Invalid balance input");
                }
            });

            return new HorizontalLayout(balanceField, setBalanceButton);
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
                    Player player = new Player(userName, isBanned, balance, isBanned);
                    players.add(player);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateBannedStatus(String playerName, boolean isBanned) throws SQLException {
        DatabaseLogic dbLogic = null;
        PreparedStatement statement = null;
        try {
            dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            Connection connection = dbLogic.getConnection();
            String sql = "UPDATE blackjack_user SET isBanned = ? WHERE user_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setBoolean(1, isBanned);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException ignored) {}
            }
            if (dbLogic != null) {
                dbLogic.closeConnection();
            }
        }
    }
    private void delete(String playerName) throws SQLException {
        DatabaseLogic dbLogic = null;
        PreparedStatement statement = null;
        try {
            dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            Connection connection = dbLogic.getConnection();
            String sql = "DELETE FROM blackjack_user WHERE user_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException ignored) {}
            }
            if (dbLogic != null) {
                dbLogic.closeConnection();
            }
        }
    }

    private void updateBalance(String playerName, double newBalance) throws SQLException {
        DatabaseLogic dbLogic = null;
        PreparedStatement statement = null;
        try {
            dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            Connection connection = dbLogic.getConnection();
            String sql = "UPDATE blackjack_user SET balance = ? WHERE user_name = ?";
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, newBalance);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException ignored) {}
            }
            if (dbLogic != null) {
                dbLogic.closeConnection();
            }
        }
    }
}

