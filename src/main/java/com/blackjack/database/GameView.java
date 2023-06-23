package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;


@PageTitle("BlackJack")
@Route("GameView")
public class GameView extends VerticalLayout implements GameEventListener {
    private Div gameTitle, backgroundContainer;

    private GameStateManager gameManager;

    Div topRightDiv;
    Div balanceText;
    Div stakeText;

    TextField stakeAmount;

    Div bottomrightDiv;

    Div eventDiv = new Div();
    private Div playerContainer;
    private Div cardStack;
    private Div dealerPointsLabel;

    private Div dealerContainer;
    private Div endState;
    private Div balance;

    private Div stake;
    private Button reset;
    Button stand;
    Button hit;
    Button raiseStake;

    private UI currentUI; // Store the UI instance
    Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer");


    public GameView() {
        currentUI = UI.getCurrent();
        UI.getCurrent().getElement().getStyle().set("width", "0px");

        //getStyle().set("background-color", "#161414");
        getStyle().set("color", "#FFFFFF");

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


        // Initialized the new containe
        gameManager = GameStateManager.getInstance();
        playerContainer = new Div();
        cardStack = new Div();
        dealerContainer = new Div();
        endState = new Div();
        stake = new Div();



        stakeAmount = new TextField("Stake amount: ");
        // Initialize game title
        gameTitle = new Div();
        gameTitle.setText("BLACKJACK");
        gameTitle.setClassName("game-title");
        stake.setText("Stake: 0");
        raiseStake = new Button("Raise Stake");
        raiseStake.setWidth("150px");
        raiseStake.addClickListener(event -> {
            if(!(stakeAmount == null || stakeAmount.isEmpty())){
                try{
                    int stake = Integer.parseInt(stakeAmount.getValue());
                    if(stake <= 0){
                        throw new Exception();
                    }
                }
                catch (Exception e){
                    Notification.show("Please enter a valid Amount!");
                }
                int stake = Integer.parseInt(stakeAmount.getValue());
                raiseStake(stake, activePlayer);
                gameManager.startGame();
            }


        });
        hit = new Button("Hit");
        hit.setWidth("150px");
        hit.setEnabled(false);
        hit.addClickListener(event -> {
            gameManager.giveCardToPlayer(activePlayer);
            Broadcaster.broadcast();
            if (GameStateManager.isHandOverPoints(activePlayer, 21)) {
                activePlayer.isOut= true;
                playerIsStanding();
                if(gameManager.isDealersTurn()){
                    gameManager.dealerPlay();
                }
            }
        });

        stand = new Button("Stand");
        stand.setWidth("150px");
        stand.setEnabled(false);
        //stand.setVisible(false);
        stand.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
            activePlayer.setStanding(true);
            playerIsStanding();

            if (gameManager.isDealersTurn()){
                gameManager.dealerPlay();
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



        setClassName("container");
        hit.setClassName("shining-button");
        reset.setClassName("shining-button");
        raiseStake.setClassName("shining-button");
        stand.setClassName("shining-button");
        playerContainer.setClassName("card-container");
        cardStack.setClassName("card-stack show-cards");

// Initialize background container
        backgroundContainer = new Div();
        backgroundContainer.setClassName("background-container");

        // Organizing the layout
        // Initialize stake label
        stake = new Div();
        stake.setText("Stake: 0");
        balance = new Div();

        topRightDiv = new Div();
        if(activePlayer != null){
            Div playerName = new Div();
            playerName.setText(activePlayer.getPlayerName());
            balanceText = new Div();
            balanceText.setText("Balance: " + activePlayer.getBalance());
            stakeText = new Div();
            stakeText.setText("Stake: " + activePlayer.getStake());
            topRightDiv.add(playerName);
            topRightDiv.add(balanceText);
            topRightDiv.add(stakeText);

        }
        topRightDiv.setClassName("top-right-corner");



        bottomrightDiv = new Div();
        bottomrightDiv.setClassName("bottom-right-corner");

        bottomrightDiv.add(eventDiv);

        // Initialize dealer points label
        dealerPointsLabel = new Div();
        dealerPointsLabel.setClassName("dealer-points-label");


        // Create display panel and add the stake, dealer points and notifications labels
        HorizontalLayout displayPanel = new HorizontalLayout();
        displayPanel.setClassName("display-panel");
        displayPanel.add(stake, dealerPointsLabel,balance);

        // Organizing the layout
        HorizontalLayout controlPanel = new HorizontalLayout();
        controlPanel.addClassName("control-panel");
        controlPanel.add(stakeAmount, raiseStake, hit, stand, reset);

        // Adding components to the background container
        backgroundContainer.add(gameTitle, topRightDiv, bottomrightDiv,  cardStack,controlPanel, playerContainer, dealerContainer, endState);

        // Adding background container to the main layout
        add(backgroundContainer);

        Broadcaster.broadcast();
    }

    public void onDealerEnd(){
        currentUI.access(() ->{
            Dealer dealer = gameManager.dealer;
            dealerPointsLabel.setText("The Dealer has: " + dealer.getCardValues() + "Points!");
            if(dealer.isOut){
                Div label2 = new Div();
                label2.setText("The Dealer has lost!");
                label2.addClassName("notifylabel");
                dealerContainer.add(label2);
            }
            dealerContainer.setClassName("dealer-container");
            bottomrightDiv.add(dealerPointsLabel, dealerContainer);
            Broadcaster.broadcast();

            for(Player player : gameManager.getPlayerList()){
                if(!player.isOut && (GameStateManager.isHandOverPoints(player, dealer.getCardValues()) || dealer.isOut)){
                    Div label = new Div();
                    label.setText(player.getPlayerName() + " has Won! + " + player.getStake()*2);
                    label.addClassName("notifylabel");
                    player.increaseBalance(2*player.getStake());
                    bottomrightDiv.add(label);

                }
                else {
                    Div label = new Div();
                    label.setText(player.getPlayerName() + " has lost!");
                    label.addClassName("notifylabel");
                    bottomrightDiv.add(label);
                }
            }
            add(endState);
            reset.setEnabled(true);
            reset.setVisible(true);
                });

    }

    private void raiseStake(int amount, Player player){
        if(activePlayer != null){
            if(player.getBalance() >= amount){

                player.increaseStake(amount);
                balanceText.setText("Balance: "+player.getBalance());
                if(stakeText != null){
                    stakeText.setText("Stake: "+ player.getStake());
                }

                player.hasIncreasedStake = true;
            }
            else{
                Notification.show("You dont have enough Cash!");
            }
        }
    }

    private void displayAllPlayersHands() {
        playerContainer.removeAll();
        if(eventDiv != null){
            eventDiv.removeAll();
        }
        List<Player> allPlayers = gameManager.getPlayerList();
        for (Player player : allPlayers) {
            Div handContainer = new Div();
            List<Card> playerHand = player.getHand();
            if (player.isOut) {
                Div playerLost = new Div();
                playerLost.setText(player.getPlayerName() + " is over 21 Points!");
                playerLost.addClassName("notifylabel");
                eventDiv.add(playerLost);
            }
            for (Card card : playerHand) {
                Image cardImage = new Image(card.imagePath, "");
                cardImage.setWidth("100px");
                //cardImage.getElement().getClassList().add("card-image");
                cardImage.getElement().setAttribute("class", "card-image fly-out");
                handContainer.add(cardImage);
            }


            balance.setText("" +player.getBalance());

            playerContainer.add(handContainer); // ADDED this line
        }
        Dealer dealer = gameManager.dealer;
        Div hand = new Div();
        //hand.setText(dealer.name);
        for (Card card: dealer.getHand()){
            Image cardImage = new Image(card.imagePath, "");
            cardImage.getElement().setAttribute("class", "card-image fly-out");
            cardImage.setWidth("50px");
            hand.add(cardImage);
        }
        playerContainer.add(hand);
    }

    public void playerIsStanding(){
        hit.setEnabled(false);
        hit.setVisible(false);
        stand.setEnabled(false);
        stand.setVisible(false);
    }

    @Override
    public void onGameStart() {
        if(currentUI.isAttached()) {
            currentUI.access(() -> {
                hit.setVisible(true);
                hit.setEnabled(true);
                stand.setEnabled(true);
                stand.setVisible(true);
                raiseStake.setVisible(false);
                raiseStake.setEnabled(false);
                stakeAmount.setVisible(false);
                stakeAmount.setEnabled(false);
                Broadcaster.broadcast();
            });
        }
    }

    @Override
    public void onGameReset() {
        if(currentUI.isAttached()){
            currentUI.access(()-> {
                // Clear the game area
                playerContainer.removeAll();
                dealerContainer.removeAll();
                bottomrightDiv.removeAll();
                endState.removeAll();
                if(stakeText != null){
                    stakeText.setText("Stake: 0");
                }
                if(activePlayer != null){
                    balanceText.setText("Balance: " +activePlayer.getBalance());
                }
                // Reset buttons
                hit.setEnabled(false);
                stand.setEnabled(false);
                reset.setVisible(false);
                reset.setEnabled(false);
                raiseStake.setVisible(true);
                raiseStake.setEnabled(true);
                stakeAmount.setEnabled(true);
                stakeAmount.setVisible(true);
                Broadcaster.broadcast();
            });
        }
    }

}
