package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    private List<Player> players = new ArrayList<>();

    public void startGame() {

        Dealer dealer = new Dealer("Dealer");


    }

    public void addPlayer(String playerName){
        players.add(new Player(playerName, false));

    }

}
