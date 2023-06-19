package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {

    private static GameStateManager instance;
    private List<Player> playerList;

    private CardDeck cards;

    private GameStateManager(){
        playerList = new ArrayList<>();
        cards = new CardDeck();
        cards.shuffle();
    }
    public static synchronized GameStateManager getInstance(){
        if(instance == null){
            instance = new GameStateManager();
        }
        return instance;
    }
    public void addPlayer(Player player){
        playerList.add(player);
    }

    public void giveCardToPlayer(IPlayer player){
        player.takeCard(cards.draw());
    }

    public List<Player> getPlayerList(){
        return playerList;
    }



}
