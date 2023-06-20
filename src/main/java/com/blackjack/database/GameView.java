package com.blackjack.database;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

@PageTitle("BlackJack")
@Route("GameView")
public class GameView extends VerticalLayout {

    private GameStateManager gameManager;
    // Added a new container to hold all players' cards
    private Div playerContainer; // ADDED this line

    public GameView() {

        Broadcaster.register(() -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                displayAllPlayersHands(); // Refresh the view
            }));
        });
        addDetachListener(detachEvent -> Broadcaster.unregister(() -> {
            getUI().ifPresent(ui -> ui.access(() -> {
                displayAllPlayersHands(); // Refresh the view
            }));
        }));

        gameManager = GameStateManager.getInstance();
        // Initialized the new container
        playerContainer = new Div(); // ADDED this line

        displayAllPlayersHands();

        Button hit = new Button("Hit");
        hit.setWidth("100px");
        hit.addClickListener(event -> {
            // Modified these lines to handle all players
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer"); // ADDED this line
            gameManager.giveCardToPlayer(activePlayer);
            Broadcaster.broadcast();
            if (GameEngine.isHandOverPoints(activePlayer, 21)) {
                checkIfDealersTurn();
                hit.setEnabled(false);
            }
        });

        Button stand = new Button("Stand");
        stand.setWidth("100px");
        stand.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer"); // ADDED this line
            activePlayer.setStanding(true);
            hit.setEnabled(false);
            stand.setEnabled(false);

            if (checkIfDealersTurn()){
                startDealerPlay();
            }

        });

        // Modified this line to add the playerContainer
        add(hit, stand, playerContainer); // CHANGED this line from add(hit, stand, handContainer);

        Broadcaster.broadcast();
        // Calling the new method
    }

    private boolean checkIfDealersTurn(){
        boolean allPlayersDone = gameManager.getPlayerList().stream()
                .allMatch(player -> GameEngine.isHandOverPoints(player,21) || player.getStanding());
        return allPlayersDone;
    }
    private void startDealerPlay(){
        Div dealerHand = new Div();
        Dealer dealer = new Dealer("Dealer");
        GameStateManager gameStateManager = GameStateManager.getInstance();
        while (!GameEngine.isHandOverPoints(dealer, 16)){

            gameStateManager.giveCardToPlayer(dealer);
        }
        Div label = new Div();
        label.setText("The Dealer has: " + dealer.getCardValues() + "Points!");
        dealerHand.add(label);

        if(GameEngine.isHandOverPoints(dealer, 21)){

            Div label2 = new Div();
            label2.setText("The Dealer has lost!");
            dealerHand.add(label2);
        }

        for (Card card : dealer.getHand()) {
            Image cardImage = new Image(card.imagePath, "");
            cardImage.setWidth("50px");
            dealerHand.add(cardImage);
        }
        add(dealerHand);
        Broadcaster.broadcast();
    }

    private void displayAllPlayersHands() { // CHANGED method name

        playerContainer.removeAll(); // CHANGED this line from handContainer.removeAll();
        // Added these lines to loop through all players and display their cards
        List<Player> allPlayers = gameManager.getPlayerList(); // ADDED this line
        for (Player player : allPlayers) {
            Div handContainer = new Div(); // ADDED this line
            List<Card> playerHand = player.getHand();
            if (GameEngine.isHandOverPoints(player, 21)) {
                Div pointsLabel = new Div();
                pointsLabel.setText(player.getPlayerName() + " lost!");
                handContainer.add(pointsLabel);
            }
            for (Card card : playerHand) {
                Image cardImage = new Image(card.imagePath, "");
                cardImage.setWidth("200px");
                //cardImage.getElement().getStyle().set("animation-name", animation.getName()); // Apply animation
                cardImage.getElement().getClassList().add("card-image"); // Add the CSS class
                handContainer.add(cardImage); // Modified this line
            }
            playerContainer.add(handContainer); // ADDED this line
        }
    }
}
