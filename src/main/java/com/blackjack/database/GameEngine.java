package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    private static List<Player> players = new ArrayList<>();

    public static void startGame(List<Player> playerList) {

        Dealer dealer = new Dealer("Dealer");
        players = playerList;

        CardDeck cards = new CardDeck();




    }


}
