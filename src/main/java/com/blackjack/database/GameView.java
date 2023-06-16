package com.blackjack.database;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.builder.Diff;


import java.util.List;


@PageTitle("BlackJack")
@Route("GameView")
public class GameView extends VerticalLayout {
    private GameStateManager gameManager;
    VaadinSession vaadinSession = VaadinSession.getCurrent();
    Player activePlayer;
    public GameView(){

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
        add(hit, stand);

        activePlayer = (Player) vaadinSession.getAttribute("activePlayer");
        initializeGame();
    }

    public void initializeGame(){

        gameManager = GameStateManager.getInstance();
        gameManager.giveCardToPlayer(activePlayer);
        displayHand();

    }

    private void displayHand() {

        List<Card> playerHand = activePlayer.getHand(); // Assuming you have a getter for the hand in the Player class
        // Display the cards to the player (e.g., in a Label or some other component)
        for (Card card : playerHand) {
            Div label = new Div();
            label.setText("You have: " + card.suit + " " + card.rank); // Assuming your Card class has a suitable toString method
            add(label);
        }
    }

}
