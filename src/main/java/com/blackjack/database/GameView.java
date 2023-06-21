package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;





import java.util.List;


@PageTitle("BlackJack")
@Route("GameView")
@JsModule("./frontend/js/fly-cards.js") // Reference to the fly-cards.js file
public class GameView extends VerticalLayout {

    private GameStateManager gameManager;
    private Div playerContainer;
    private Div cardStack; // Added a Div to represent the card stack


    public GameView() {

        Broadcaster.register(() -> {
            getUI().ifPresent(ui -> ui.access(this::displayAllPlayersHands));
        });
        addDetachListener(detachEvent -> Broadcaster.unregister(() -> {
            getUI().ifPresent(ui -> ui.access(this::displayAllPlayersHands));
        }));

        gameManager = GameStateManager.getInstance();
        playerContainer = new Div();
        cardStack = new Div(); // Initialized the card stack container


        displayAllPlayersHands();

        Button hit = new Button("Hit");
        hit.setWidth("100px");
        hit.getElement().setAttribute("class", "button hit");
        hit.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
            gameManager.giveCardToPlayer(activePlayer);
            Broadcaster.broadcast();
            if (GameEngine.isHandOverPoints(activePlayer, 21)) {
                checkIfDealersTurn();
                hit.setEnabled(false);
            }
        });

        Button stand = new Button("Stand");
        stand.setWidth("100px");
        stand.getElement().setAttribute("class", "button stand");
        stand.addClickListener(event -> {
            Player activePlayer = (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
            activePlayer.setStanding(true);
            hit.setEnabled(false);
            stand.setEnabled(false);

            if (checkIfDealersTurn()) {
                startDealerPlay();
            }

        });

        Div buttonContainer = new Div();
        buttonContainer.add(hit, stand);

        setClassName("container");
        hit.setClassName("button-container");
        stand.setClassName("button-container");
        playerContainer.setClassName("card-container");
        cardStack.setClassName("card-stack"); // Added CSS class for the card stack

        add(buttonContainer, playerContainer, cardStack);



        Broadcaster.broadcast();
    }

    private boolean checkIfDealersTurn() {
        boolean allPlayersDone = gameManager.getPlayerList().stream()
                .allMatch(player -> GameEngine.isHandOverPoints(player, 21) || player.getStanding());
        return allPlayersDone;
    }

    private void startDealerPlay() {
        Div dealerHand = new Div();
        Dealer dealer = new Dealer("Dealer");
        Div dealerHandContainer = new Div(); // Create a container for the dealer's hand
        GameStateManager gameStateManager = GameStateManager.getInstance();
        while (!GameEngine.isHandOverPoints(dealer, 16)) {
            gameStateManager.giveCardToPlayer(dealer);
        }
        Div label = new Div();
        label.setText("The Dealer has: " + dealer.getCardValues() + "Points!");
        dealerHand.add(label);

        if (GameEngine.isHandOverPoints(dealer, 21)) {
            Div label2 = new Div();
            label2.setText("The Dealer has lost!");
            dealerHand.add(label2);
        }

        for (Card card : dealer.getHand()) {
            Image cardImage = new Image(card.imagePath, "");
            cardImage.getElement().getClassList().add("card-image");
            cardImage.setWidth("100px");
            dealerHand.add(cardImage);
        }
        dealerHandContainer.add(dealerHand); // Add the dealer's cards to the dealer's hand container
        dealerHandContainer.setClassName("card-container"); // Use the same card container class as the players class as the players
        add(dealerHand);
        Broadcaster.broadcast();
    }

    private void displayAllPlayersHands() {
        playerContainer.removeAll();
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
    private void animateCards() {
        // Invoke JavaScript code to trigger the card fly animation
        //getElement().executeJs("animateCardFly();");
    }


}
