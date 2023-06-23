package com.blackjack.database;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.button.Button;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@PageTitle("Waiting Lobby")
@Route("waiting-lobby")
@CssImport("./themes/mytodo/lobby.css")
public class Lobby extends VerticalLayout {
    private static final long serialVersionUID = 503398040364625051L;
    private static final List<Player> activePlayers = new CopyOnWriteArrayList<>();
    private Grid<Player> playersGrid = new Grid<>();

    public Lobby() {
        // Add the lobby-container class to this VerticalLayout
        this.addClassName("lobby-container");

        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        Broadcaster.register(this::updateGrid);
        H2 title = new H2("WAITING LOBBY");
        playersGrid.setWidthFull();
        playersGrid.getStyle().set("opacity", "0.75");
        playersGrid.addColumn(Player::getPlayerName).setHeader("Name");
        playersGrid.addColumn(new ComponentRenderer<>(player -> {
            Button startButton = new Button("Start");
            // Use the correct class name for the start button
            startButton.addClassName("start-button");
            if (!player.equals(getActivePlayer())) {
                startButton.setEnabled(false);
            }
            startButton.addClickListener(event -> {
                GameStateManager gameStateManager = GameStateManager.getInstance();
                gameStateManager.addPlayer(player);
                gameStateManager.giveCardToPlayer(player);
                gameStateManager.giveCardToPlayer(player);
                UI.getCurrent().navigate("GameView");
            });
            return startButton;
        })).setHeader("Start Game");

        HorizontalLayout gridWrapper = new HorizontalLayout(playersGrid);
        gridWrapper.setJustifyContentMode(JustifyContentMode.CENTER);

        playersGrid.setPageSize(7);
        playersGrid.setItems(activePlayers);
        add(title, playersGrid);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    public static void playerLoggedIn(Player player) {
        boolean isAlreadyPresent = activePlayers.stream()
                .anyMatch(existingPlayer -> existingPlayer.equals(player));

        if (!isAlreadyPresent) {
            if (activePlayers.size() >= 7) {
                Notification.show("Lobby full, please wait for the next session!", 3000, Notification.Position.MIDDLE);
            } else {
                activePlayers.add(player);
                Broadcaster.broadcast(activePlayers);
            }
        }
    }

    private void updateGrid(List<Player> players) {
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                playersGrid.setItems(players);
                playersGrid.getDataProvider().refreshAll();
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        Broadcaster.unregister(this::updateGrid);
        super.onDetach(detachEvent);
    }

    private boolean isLoggedIn() {
        return getActivePlayer() != null;
    }

    private Player getActivePlayer() {
        return (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
    }
}
