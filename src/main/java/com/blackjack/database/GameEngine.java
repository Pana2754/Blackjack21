package com.blackjack.database;

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
        for (Player player: players) {
            player.takeCard(cards.draw());
        }
    }

    public void hitCard(){

    }


}
