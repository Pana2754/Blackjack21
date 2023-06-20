package com.blackjack.database;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@PageTitle("Waiting Lobby")
@Route("waiting-lobby")
public class Lobby extends VerticalLayout {

    private static final long serialVersionUID = 503398040364625051L;
    private static final List<Player> activePlayers = new CopyOnWriteArrayList<>();
    private final Grid<Player> playersGrid = new Grid<>();

    public Lobby() {
        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        Broadcaster.register(this::updateGrid);

        Image logo = createLogo();
        H2 title = new H2("Waiting Lobby");

        configurePlayersGrid();

        add(logo, title, playersGrid);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMargin(true);
        setSpacing(true);
        setWidth("100%");
        setPadding(true);
        setSpacing(true);
    }

    private Image createLogo() {
        Image logo = new Image("head.png", "Logo");
        logo.setWidth("150px");
        logo.setHeight("150px");
        return logo;
    }

    private void configurePlayersGrid() {
        playersGrid.addColumn(Player::getPlayerName).setHeader("Name");
        playersGrid.setPageSize(7);

        playersGrid.addComponentColumn(player -> {
            Button startGame = new Button("START");
            startGame.setEnabled(activePlayers.size() < 7);
            startGame.addClickListener(event -> startGame(player));
            return startGame;
        }).setHeader("Start Game");

        playersGrid.setItems(activePlayers);
    }

    private void startGame(Player activePlayer) {
        GameStateManager gameStateManager = GameStateManager.getInstance();
        gameStateManager.addPlayer(activePlayer);
        gameStateManager.giveCardToPlayer(getActivePlayer());
        gameStateManager.giveCardToPlayer(getActivePlayer());
        UI.getCurrent().navigate("GameView");
    }

    public static void playerLoggedIn(Player player) {
        if (!isPlayerInList(player, activePlayers)) {
            activePlayers.add(player);
            Broadcaster.broadcast(activePlayers);
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

    private static boolean isPlayerInList(Player player, List<Player> playerList) {
        return playerList.stream().anyMatch(p -> p.getPlayerName().equals(player.getPlayerName()));
    }
}
