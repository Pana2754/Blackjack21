package com.blackjack.database;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.component.button.Button;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@PageTitle("Waiting Lobby")
@Route("waiting-lobby")
public class Lobby extends VerticalLayout {
    private static final long serialVersionUID = 503398040364625051L;
    private static final List<Player> activePlayers = new CopyOnWriteArrayList<>();
    private Grid<Player> playersGrid = new Grid<>();

    public Lobby() {
        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        Broadcaster.register(this::updateGrid);

        Image logo = new Image("head.png", "Logo");
        logo.setWidth("150px");
        logo.setHeight("150px");

        H2 title = new H2("Waiting Lobby");

        playersGrid.addColumn(Player::getPlayerName).setHeader("Name");
        playersGrid.addComponentColumn(this::createReadyCheckbox).setHeader("Ready");
        HorizontalLayout gridWrapper = new HorizontalLayout(playersGrid);
        gridWrapper.setJustifyContentMode(JustifyContentMode.CENTER);

        Player activePlayer = getActivePlayer();
        if (activePlayer != null && !activePlayers.contains(activePlayer)) {
            activePlayers.add(activePlayer);
        }


        Button startGame = new Button("START");
        startGame.setWidth("100px");
        startGame.addClickListener(event -> {
            GameStateManager gameStateManager = GameStateManager.getInstance();
            gameStateManager.addPlayers(activePlayers);
            gameStateManager.giveCardToPlayer(getActivePlayer());
            gameStateManager.giveCardToPlayer(getActivePlayer());
            UI.getCurrent().navigate("GameView");

        });

        // Update the Grid with all the active players
        playersGrid.setItems(activePlayers);
        add(logo, title, playersGrid, startGame);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMargin(true);
        setSpacing(true);
        setWidth("100%"); // Set the width of the container to 100%
        setPadding(true); // Add padding around the elements
        setSpacing(true); // Add spacing between the elements

    }

    public static void playerLoggedIn(Player player) {
        activePlayers.add(player);
        Broadcaster.broadcast(activePlayers);
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

    private Checkbox createReadyCheckbox(Player player) {
        Checkbox checkbox = new Checkbox();
        checkbox.setValue(player.isReady());
        checkbox.addValueChangeListener(event -> {
            player.setReady(event.getValue());
            Broadcaster.broadcast(activePlayers);
        });
        return checkbox;
    }
}
