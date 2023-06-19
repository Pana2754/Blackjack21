package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@PageTitle("BlackJack")
@Route("GameView")
public class GameView extends VerticalLayout {

    private GameStateManager gameManager;
    // Added a new container to hold all players' cards
    private Div playerContainer; // ADDED this line

    public GameView() {
        gameManager = GameStateManager.getInstance();
        // Initialized the new container
        playerContainer = new Div(); // ADDED this line

        Button hit = new Button("Hit");
        hit.setWidth("100px");
        hit.addClickListener(event -> {
            // Modified these lines to handle all players
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer"); // ADDED this line
            if (GameEngine.isHandOver21Points(activePlayer)) {
                return;
            }
            gameManager.giveCardToPlayer(activePlayer);
            displayAllPlayersHands(); // CHANGED this line from displayHand();
        });

        Button stand = new Button("Stand");
        stand.setWidth("100px");
        stand.addClickListener(event -> {
        });

        // Modified this line to add the playerContainer
        add(hit, stand, playerContainer); // CHANGED this line from add(hit, stand, handContainer);

        // Calling the new method
        displayAllPlayersHands(); // CHANGED this line from displayHand();
    }

    // Replaced displayHand method with displayAllPlayersHands
    private void displayAllPlayersHands() { // CHANGED method name
        playerContainer.removeAll(); // CHANGED this line from handContainer.removeAll();

        // Added these lines to loop through all players and display their cards
        List<Player> allPlayers = gameManager.getPlayerList(); // ADDED this line
        for (Player player : allPlayers) {
            Div handContainer = new Div(); // ADDED this line
            List<Card> playerHand = player.getHand();
            if (GameEngine.isHandOver21Points(player)) {
                Div pointsLabel = new Div();
                pointsLabel.setText(player.getPlayerName() + " lost!");
                handContainer.add(pointsLabel);
            }
            for (Card card : playerHand) {
                Div label = new Div();
                label.setText(player.getPlayerName() + " has: " + card.suit + " " + card.rank);
                handContainer.add(label);
            }
            playerContainer.add(handContainer); // ADDED this line
        }
    }
}
