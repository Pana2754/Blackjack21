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
    private Div dealerContainer;
    private Div endState;
    private Button reset;

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
        dealerContainer = new Div();
        endState = new Div();

        displayAllPlayersHands();

        Button hit = new Button("Hit");
        hit.setWidth("150px");
        hit.addClickListener(event -> {
            // Modified these lines to handle all players
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer"); // ADDED this line
            gameManager.giveCardToPlayer(activePlayer);
            Broadcaster.broadcast();
            if (GameStateManager.isHandOverPoints(activePlayer, 21)) {
                activePlayer.isOut= true;
                if(gameManager.isDealersTurn()){
                    startDealerPlay();
                }
                hit.setEnabled(false);
            }
        });

        Button stand = new Button("Stand");
        stand.setWidth("150px");
        stand.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer"); // ADDED this line
            activePlayer.setStanding(true);
            hit.setEnabled(false);
            stand.setEnabled(false);

            if (gameManager.isDealersTurn()){
                startDealerPlay();
            }

        });

        reset = new Button("New Game");
        reset.setWidth("150px");
        reset.setVisible(false);
        reset.setEnabled(false);
        reset.addClickListener(event -> {
            reset.setVisible(false);
            reset.setEnabled(false);
            hit.setEnabled(true);
            stand.setEnabled(true);
            gameManager.resetGame();
            dealerContainer.removeAll();
            endState.removeAll();
            displayAllPlayersHands();
        });
        // Modified this line to add the playerContainer
        add(hit, stand, reset, playerContainer); // CHANGED this line from add(hit, stand, handContainer);

        Broadcaster.broadcast();
        // Calling the new method
    }

    private void startDealerPlay(){
        Dealer dealer = new Dealer("Dealer");
        GameStateManager gameStateManager = GameStateManager.getInstance();
        while (!GameStateManager.isHandOverPoints(dealer, 16)){

            gameStateManager.giveCardToPlayer(dealer);
        }
        Div label = new Div();
        label.setText("The Dealer has: " + dealer.getCardValues() + "Points!");
        dealerContainer.add(label);

        if(GameStateManager.isHandOverPoints(dealer, 21)){
            dealer.isOut= true;
            Div label2 = new Div();
            label2.setText("The Dealer has lost!");
            dealerContainer.add(label2);
        }

        for (Card card : dealer.getHand()) {
            Image cardImage = new Image(card.imagePath, "");
            cardImage.setWidth("50px");
            dealerContainer.add(cardImage);
        }

        add(dealerContainer);
        Broadcaster.broadcast();

        GameEnd(dealer);
    }

    public void GameEnd(Dealer dealer){

        for(Player player : gameManager.getPlayerList()){
            if(!player.isOut && (GameStateManager.isHandOverPoints(player, dealer.getCardValues()) || dealer.isOut)){
                Div label = new Div();
                label.setText(player.getPlayerName() + " has Won!");
                endState.add(label);
            }
            else {
                Div label = new Div();
                label.setText(player.getPlayerName() + " has lost!");
                endState.add(label);
            }
        }
        add(endState);
        reset.setEnabled(true);
        reset.setVisible(true);


    }

    private void displayAllPlayersHands() { // CHANGED method name

        playerContainer.removeAll(); // CHANGED this line from handContainer.removeAll();
        // Added these lines to loop through all players and display their cards
        List<Player> allPlayers = gameManager.getPlayerList(); // ADDED this line
        for (Player player : allPlayers) {
            Div handContainer = new Div(); // ADDED this line
            List<Card> playerHand = player.getHand();
            if (GameStateManager.isHandOverPoints(player, 21)) {
                Div pointsLabel = new Div();
                pointsLabel.setText(player.getPlayerName() + " is Over 21 Points!");
                handContainer.add(pointsLabel);
            }
            for (Card card : playerHand) {
                Image cardImage = new Image(card.imagePath, "");
                cardImage.setWidth("50px");
                handContainer.add(cardImage); // Modified this line
            }
            playerContainer.add(handContainer); // ADDED this line
        }
    }

}
