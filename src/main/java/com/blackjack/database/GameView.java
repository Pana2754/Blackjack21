package com.blackjack.database;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;
@PreserveOnRefresh
@PageTitle("BlackJack")
@Route("GameView")
public class GameView extends VerticalLayout implements GameEventListener {

    private GameStateManager gameManager;

    private final Runnable updateUIRunnable;
    // Added a new container to hold all players' cards
    private Div playerContainer; // ADDED this line
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

        updateUIRunnable = () -> {
            if (currentUI.isAttached()) {
                currentUI.access(() -> {
                    displayAllPlayersHands();
                });
            }
        };
        gameManager = GameStateManager.getInstance();

        // Initialized the new container
        playerContainer = new Div(); // ADDED this line
        dealerContainer = new Div();
        endState = new Div();
        stake = new Div();
        stake.setText("Stake: 0");

        raiseStake10 = new Button("Put10");
        raiseStake10.setWidth("150px");
        raiseStake10.addClickListener(event -> {
            gameManager.raiseStake(10, activePlayer);
            stake.setText("Stake: "+ activePlayer.getStake());
        });
        hit = new Button("Hit");
        hit.setWidth("150px");
        hit.addClickListener(event -> {
            gameManager.giveCardToPlayer(activePlayer);
            if(activePlayer.isOut){
                playerIsStanding();
            }
            if(gameManager.isDealersTurn()){
                    startDealerPlay();
            }
        });
        stand = new Button("Stand");
        stand.setWidth("150px");
        stand.addClickListener(event -> {
            activePlayer.setStanding(true);
            playerIsStanding();
            if (gameManager.isDealersTurn()){
                startDealerPlay();
            }
        });
        reset = new Button("New Game");
        reset.setWidth("150px");
        reset.addClickListener(event -> {
            gameManager.resetGame();
        });

        // Modified this line to add the playerContainer
        add(hit, stand, reset, playerContainer,stake, dealerContainer, raiseStake10); // CHANGED this line from add(hit, stand, handContainer);
    }

    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if(activePlayer != null){
            // Register the view with the broadcaster here
            Broadcaster.register(updateUIRunnable);
            Broadcaster.addGameEventListener(this);
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Unregister the view from the broadcaster here
        Broadcaster.unregister(updateUIRunnable);
        Broadcaster.removeGameEventListener(this);
    }


    private void startDealerPlay(){
        Dealer dealer = gameManager.dealer;
        GameStateManager gameStateManager = GameStateManager.getInstance();
        while (!gameManager.isHandOverPoints(dealer, 16)){

            gameStateManager.giveCardToPlayer(dealer);
        }
        Div label = new Div();
        label.setText("The Dealer has: " + dealer.getCardValues() + "Points!");
        dealerContainer.add(label);

        if(gameManager.isHandOverPoints(dealer, 21)){
            dealer.isOut= true;
            Div label2 = new Div();
            label2.setText("The Dealer has lost!");
            dealerContainer.add(label2);
        }


        Broadcaster.broadcast();
        Broadcaster.gameEnd();
    }

    public void playerIsOut(IPlayer player){

        Notification.show(player.getPlayerName()+ " is OUT!");
        //Notification.show("test");
    }

    private void displayAllPlayersHands() { // CHANGED method name

        playerContainer.removeAll(); // CHANGED this line from handContainer.removeAll();
        // Added these lines to loop through all players and display their cards
        List<Player> allPlayers = gameManager.getPlayerList(); // ADDED this line
        for (Player player : allPlayers) {
            Div handContainer = new Div();
            handContainer.setText(player.getPlayerName());// ADDED this line
            List<Card> playerHand = player.getHand();
            for (Card card : playerHand) {
                Image cardImage = new Image(card.imagePath, "");
                cardImage.setWidth("50px");
                handContainer.add(cardImage); // Modified this line
            }
            Div balance = new Div();
            balance.setText("" +player.getBalance());
            handContainer.add(balance);
            playerContainer.add(handContainer); // ADDED this line
        }
        dealerContainer.removeAll();
        Dealer dealer = gameManager.dealer;
        Div hand = new Div();
        hand.setText(dealer.name);
        for (Card card: dealer.getHand()){
            Image cardImage = new Image(card.imagePath, "");
            cardImage.setWidth("50px");
            hand.add(cardImage);
        }
        dealerContainer.add(hand);
    }

    public void onStakeRoundStart(){
        if(currentUI.isAttached()){
            currentUI.access(() -> {
                hit.setVisible(false);
                hit.setEnabled(false);
                stand.setEnabled(false);
                stand.setVisible(false);
                reset.setVisible(false);
                reset.setEnabled(false);
                raiseStake10.setVisible(true);
                raiseStake10.setEnabled(true);
            });
        }
    }

    @Override
    public void onGameStart() {
        if (currentUI.isAttached()) {
            currentUI.access(() -> {
                hit.setVisible(true);
                hit.setEnabled(true);
                stand.setEnabled(true);
                stand.setVisible(true);
                raiseStake10.setVisible(false);
                raiseStake10.setEnabled(false);

            });
        }
        Broadcaster.broadcast();
    }

    public void playerIsStanding(){
        hit.setEnabled(false);
        hit.setVisible(false);
        stand.setEnabled(false);
        stand.setVisible(false);
    }

    public void onGameEnd(){
        if(currentUI.isAttached()){

            Dealer dealer = gameManager.dealer;
            Div end = new Div();
            for(Player player : gameManager.getPlayerList()){
                if(!player.isOut && (gameManager.isHandOverPoints(player, dealer.getCardValues()) || dealer.isOut)){
                    Div label = new Div();
                    label.setText(player.getPlayerName() + " has Won! + " + player.getStake()*2);
                    player.increaseBalance(2*player.getStake());
                    end.add(label);

                }
                else {
                    Div label = new Div();
                    label.setText(player.getPlayerName() + " has lost!");
                    end.add(label);
                }
            }


            currentUI.access(()-> {
                endState.add(end);
                add(endState);
                reset.setEnabled(true);
                reset.setVisible(true);
            });
        }



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

        });
        Broadcaster.broadcast();

    }

}
