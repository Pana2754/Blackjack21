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
    VaadinSession vaadinSession = VaadinSession.getCurrent();
    private static Player activePlayer;
    private Div handContainer; // container to hold the labels of the cards

    public GameView() {
        gameManager = GameStateManager.getInstance();
        handContainer = new Div(); // initialize the container

        Button hit = new Button("Hit");
        hit.setWidth("100px");
        hit.addClickListener(event -> {
            gameManager.giveCardToPlayer(activePlayer);
            displayHand();
        });

        Button stand = new Button("Stand");
        stand.setWidth("100px");
        stand.addClickListener(event -> {
        });

        add(hit, stand, handContainer); // add the container to the layout
        activePlayer = (Player) vaadinSession.getAttribute("activePlayer");
        displayHand();
    }

    private void displayHand() {
        if (activePlayer != null) {
            handContainer.removeAll(); // remove all old labels

            List<Card> playerHand = activePlayer.getHand(); // Assuming you have a getter for the hand in the Player class
            // Display the cards to the player (e.g., in a Label or some other component)
            for (Card card : playerHand) {
                Div label = new Div();
                label.setText("You have: " + card.suit + " " + card.rank); // Assuming your Card class has suit and rank properties
                handContainer.add(label); // add the label to the container
            }
        }
    }
}
