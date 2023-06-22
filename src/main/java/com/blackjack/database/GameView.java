package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;


@PageTitle("BlackJack")
@Route("GameView")
public class GameView extends VerticalLayout implements GameEventListener {

    private GameStateManager gameManager;
    private Div playerContainer;
    private Div cardStack; // Added a Div to represent the card stack

    // Added a new container to hold all players' cards
    //private Div playerContainer; // ADDED this line
    private Div dealerContainer;
    private Div endState;

    private Div stake;
    private Button reset;
    Button stand;
    Button hit;
    Button raiseStake10;

    private UI currentUI; // Store the UI instance
    Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer");

    public GameView() {

        currentUI = UI.getCurrent();

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
        Broadcaster.addGameEventListener(this);

        gameManager = GameStateManager.getInstance();
        playerContainer = new Div();
        cardStack = new Div(); // Initialized the card stack container

        // Initialized the new container
        //playerContainer = new Div(); // ADDED this line
        dealerContainer = new Div();
        endState = new Div();
        stake = new Div();
        stake.setText("Stake: 0");

        displayAllPlayersHands();
        raiseStake10 = new Button("Put10");
        raiseStake10.setWidth("150px");
        raiseStake10.addClickListener(event -> {
            raiseStake(10, activePlayer);
            gameManager.startGame();
        });
        hit = new Button("Hit");
        hit.setWidth("150px");
        hit.setEnabled(false);
        //hit.setVisible(false);
        hit.addClickListener(event -> {
            // Modified these lines to handle all players
             // ADDED this line
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

        stand = new Button("Stand");
        stand.setWidth("150px");
        stand.setEnabled(false);
        //stand.setVisible(false);
        stand.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
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
        add(hit, stand, reset, playerContainer,stake, raiseStake10); // CHANGED this line from add(hit, stand, handContainer);
        Div buttonContainer = new Div();
        buttonContainer.add(hit, stand);

        setClassName("container");
        hit.setClassName("shining-button");
        stand.setClassName("shining-button");
        playerContainer.setClassName("card-container");
        cardStack.setClassName("card-stack show-cards");
        // Added CSS class for the card stack


        add(hit,stand, playerContainer, cardStack);



        Broadcaster.broadcast();
    }

    private void startDealerPlay(){
        Dealer dealer = gameManager.dealer;
        GameStateManager gameStateManager = GameStateManager.getInstance();
        while (!GameStateManager.isHandOverPoints(dealer, 16)){

            gameStateManager.giveCardToPlayer(dealer);
        }
        Div label = new Div();
        label.setText("The Dealer has: " + dealer.getCardValues() + "Points!");
        label.addClassName("notifylabel");
        dealerContainer.add(label);

        if(GameStateManager.isHandOverPoints(dealer, 21)){
            dealer.isOut= true;
            Div label2 = new Div();
            label2.setText("The Dealer has lost!");
            label2.addClassName("notifylabel");
            dealerContainer.add(label2);
        }

        add(dealerContainer);
        Broadcaster.broadcast();

        GameEnd(dealer);
    }

    public void GameEnd(Dealer dealer){

        for(Player player : gameManager.getPlayerList()){
            if(!player.isOut && (GameStateManager.isHandOverPoints(player, dealer.getCardValues()) || dealer.isOut)){
                Div label = new Div();
                label.setText(player.getPlayerName() + " has Won! + " + player.getStake()*2);
                label.addClassName("notifylabel");
                player.increaseBalance(2*player.getStake());
                endState.add(label);

            }
            else {
                Div label = new Div();
                label.setText(player.getPlayerName() + " has lost!");
                label.addClassName("notifylabel");
                endState.add(label);
            }
        }
        add(endState);
        reset.setEnabled(true);
        reset.setVisible(true);


    }

    private void raiseStake(int amount, Player player){
        if(player.getBalance() >= amount){
            player.increaseStake(amount);
            stake.setText("Stake: "+ player.getStake());
            player.hasIncreasedStake = true;
        }
        else{
            Notification.show("You dont have enough Cash!");
        }

    }

    private void displayAllPlayersHands() {
        playerContainer.removeAll();
        List<Player> allPlayers = gameManager.getPlayerList();
        for (Player player : allPlayers) {
            Div handContainer = new Div();
            List<Card> playerHand = player.getHand();
            if (GameStateManager.isHandOverPoints(player, 21)) {
                Div pointsLabel = new Div();
                pointsLabel.setText(player.getPlayerName() + " lost!");
                pointsLabel.addClassName("notifylabel");
                handContainer.add(pointsLabel);
            }
            for (Card card : playerHand) {
                Image cardImage = new Image(card.imagePath, "");
                cardImage.setWidth("100px");
                //cardImage.getElement().getClassList().add("card-image");
                cardImage.getElement().setAttribute("class", "card-image fly-out");
                handContainer.add(cardImage);
            }

            playerContainer.add(handContainer);
            Div balance = new Div();
            balance.setText("" +player.getBalance());
            handContainer.add(balance);
            playerContainer.add(handContainer); // ADDED this line
        }
        Dealer dealer = gameManager.dealer;
        Div hand = new Div();
        hand.setText(dealer.name);
        for (Card card: dealer.getHand()){
            Image cardImage = new Image(card.imagePath, "");
            cardImage.setWidth("50px");
            hand.add(cardImage);
        }
        playerContainer.add(hand);
    }

    @Override
    public void onGameStart() {
        currentUI.access(() -> {
            hit.setVisible(true);
            hit.setEnabled(true);
            stand.setEnabled(true);
            stand.setVisible(true);
            raiseStake10.setVisible(false);
            raiseStake10.setEnabled(false);


            displayAllPlayersHands();
            Broadcaster.broadcast();
        });
    }

    @Override
    public void onGameReset() {
        currentUI.access(()-> {
            // Clear the game area
            playerContainer.removeAll();
            dealerContainer.removeAll();
            endState.removeAll();
            stake.setText("Stake: 0");
            // Reset buttons
            hit.setEnabled(false);
            stand.setEnabled(false);
            reset.setVisible(false);
            reset.setEnabled(false);
            raiseStake10.setVisible(true);
            raiseStake10.setEnabled(true);

            displayAllPlayersHands();
            Broadcaster.broadcast();
        });

    }

}
