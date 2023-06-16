package com.blackjack.database;

import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    private static List<Player> players = new ArrayList<>();

    public static void startGame(List<Player> playerList) {

        Dealer dealer = new Dealer("Dealer");
        players = playerList;
        startRound();


    }

    private static void startRound(){
        CardDeck cards = new CardDeck();
        cards.shuffle();
        // Somewhere in your game interface code
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        Player activePlayer = vaadinSession.getAttribute(Player.class);

// Get the card assigned to this player
        GameStateManager gameManager = GameStateManager.getInstance();
        Card card = cards.draw();

// Display the card to the player (e.g., with a Label)
        Label cardLabel = new Label("Your card is: " + card.suit + " " + card.rank);

        }


    public void hitCard(){

    }


}
