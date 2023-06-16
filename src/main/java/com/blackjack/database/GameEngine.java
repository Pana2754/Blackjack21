package com.blackjack.database;

import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    private List<Player> players = new ArrayList<>();

    public void startGame() {

        Dealer dealer = new Dealer("Dealer");
        startRound();


    }

    private void startRound(){
        CardDeck cards = new CardDeck();
        cards.shuffle();
        // Somewhere in your game interface code


        // Get the card assigned to this player
        GameStateManager gameManager = GameStateManager.getInstance();
        Card card = cards.draw();


    }



    public void hitCard(){

    }


}
