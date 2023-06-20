package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
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
    private Div cardStackContainer; // Container for the card stack

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
        cardStackContainer = new Div(); // Initialize the card stack container

        displayAllPlayersHands();


        Button hit = new Button("Hit");
        hit.setWidth("100px");
        hit.getElement().setAttribute("class", "button hit");//add classname
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
        stand.getElement().setAttribute("class", "button stand");//add classname
        stand.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer"); // ADDED this line
            activePlayer.setStanding(true);
            hit.setEnabled(false);
            stand.setEnabled(false);

            if (checkIfDealersTurn()){
                startDealerPlay();
            }

        });

        // Create a container to hold the buttons
        Div buttonContainer = new Div();
        buttonContainer.add(hit, stand); // Add buttons to the container

        setClassName("container");
        hit.setClassName("button-container");
        stand.setClassName("button-container");
        playerContainer.setClassName("card-container");
        cardStackContainer.setClassName("card-container"); // Apply the card container style to the card stack container

        add(buttonContainer, cardStackContainer, playerContainer); // Add the card stack container to the layout

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
            cardImage.setWidth("100px");
            dealerHand.add(cardImage);
        }
        add(dealerHand);
        Broadcaster.broadcast();
    }



    private void displayAllPlayersHands() {
        playerContainer.removeAll(); // Clear the player's card container

        Div cardStack = new Div(); // Create a card stack container
        cardStack.setClassName("card-stack"); // Apply the card stack CSS class
        cardStackContainer.removeAll(); // Clear the card stack container
        cardStackContainer.add(cardStack); // Add the card stack container to the card stack container

        List<Player> allPlayers = gameManager.getPlayerList();
        for (Player player : allPlayers) {
            Div handContainer = new Div();
            List<Card> playerHand = player.getHand();
            if (GameEngine.isHandOverPoints(player, 21)) {
                Div pointsLabel = new Div();
                pointsLabel.setText(player.getPlayerName() + " lost!");
                handContainer.add(pointsLabel);
            }
            for (Card card : playerHand) {
                Image cardImage = new Image(card.imagePath, "");
                cardImage.setWidth("100px");
                cardImage.getElement().getClassList().add("card-image");
                handContainer.add(cardImage);
            }
            playerContainer.add(handContainer);
        }
    }
}

